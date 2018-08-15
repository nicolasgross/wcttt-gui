package de.nicolasgross.wcttt.gui.controller;

import de.nicolasgross.wcttt.gui.WctttGuiFatalException;
import de.nicolasgross.wcttt.gui.model.Model;
import de.nicolasgross.wcttt.lib.model.*;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class EditRoomsController extends SubscriberController<Boolean> {

	@FXML
	private ListView<Room> roomListView;
	@FXML
	private VBox editVBox;
	@FXML
	private Button addRoomButton;
	@FXML
	private TextField nameField;
	@FXML
	private CheckBox internalCheckBox;
	@FXML
	private TextField capacityField;
	@FXML
	private ChoiceBox<Chair> holderChoiceBox;
	@FXML
	private Accordion featuresAccordion;
	@FXML
	private ChoiceBox<Integer> projectorsChoiceBox;
	@FXML
	private CheckBox pcPoolCheckBox;
	@FXML
	private CheckBox teacherPcCheckBox;
	@FXML
	private CheckBox docCamCheckBox;
	@FXML
	private Button applyButton;

	@FXML
	protected void initialize() {
		roomListView.getSelectionModel().setSelectionMode(
				SelectionMode.MULTIPLE);

		ContextMenu contextMenu = new ContextMenu();
		MenuItem deleteMenuItem = new MenuItem("Delete");
		deleteMenuItem.setOnAction(event -> {
			List<Room> selection =
					roomListView.getSelectionModel().getSelectedItems();
			boolean confirmed = Util.confirmationAlert("Confirm deletion of " +
					"rooms", "Are you sure you want to delete the " +
					"selected room" + (selection.size() == 1 ? "" : "s")
					+ "?");
			if (confirmed) {
				for (Room room : new LinkedList<>(selection)) {
					try {
						if (room instanceof InternalRoom) {
							getModel().removeInternalRoom((InternalRoom) room);
						} else {
							getModel().removeExternalRoom((ExternalRoom) room);
						}
					} catch (WctttModelException e) {
						throw new WctttGuiFatalException("Implementation " +
								"error, null was passed as parameter", e);
					}
				}
			}
		});
		contextMenu.getItems().add(deleteMenuItem);

		roomListView.getSelectionModel().selectedItemProperty().addListener(
				(observable, oldValue, newValue) -> {
					updateEditVBox(newValue);
				});

		roomListView.setCellFactory(param -> {
			ListCell<Room> cell = new ListCell<>();

			cell.textProperty().bind(Bindings.when(cell.emptyProperty()).
					then("").otherwise(cell.itemProperty().asString()));

			cell.contextMenuProperty().bind(
					Bindings.when(cell.emptyProperty())
							.then((ContextMenu) null)
							.otherwise(contextMenu)
			);
			return cell;
		});

		capacityField.disableProperty().bind(
				internalCheckBox.selectedProperty().not());
		holderChoiceBox.disableProperty().bind(
				internalCheckBox.selectedProperty().not());
		featuresAccordion.disableProperty().bind(
				internalCheckBox.selectedProperty().not());

		addRoomButton.setOnAction(event -> {
			try {
				getModel().addInternalRoom(new InternalRoom());
			} catch (WctttModelException e) {
				throw new WctttGuiFatalException("Implementation error, " +
						"adding default room throws exception", e);
			}
		});

		applyButton.setOnAction(event -> applyButtonAction());
	}

	private void updateEditVBox(Room newValue) {
		if (newValue == null) {
			editVBox.disableProperty().setValue(true);
			nameField.setText("");
		} else {
			editVBox.disableProperty().setValue(false);
			nameField.setText(newValue.getName());
		}
		if (newValue instanceof InternalRoom) {
			InternalRoom selected = (InternalRoom) newValue;
			internalCheckBox.selectedProperty().setValue(true);
			capacityField.setText(String.valueOf(
					selected.getCapacity()));
			holderChoiceBox.setValue(
					selected.getHolder().orElse(null));
			projectorsChoiceBox.setValue(
					selected.getFeatures().getProjectors());
			pcPoolCheckBox.selectedProperty().setValue(
					selected.getFeatures().isPcPool());
			teacherPcCheckBox.selectedProperty().setValue(
					selected.getFeatures().hasTeacherPc());
			docCamCheckBox.selectedProperty().setValue(
					selected.getFeatures().hasDocCam());
		} else {
			internalCheckBox.selectedProperty().setValue(false);
			capacityField.setText("");
			holderChoiceBox.setValue(null);
			projectorsChoiceBox.setValue(0);
			pcPoolCheckBox.selectedProperty().setValue(false);
			teacherPcCheckBox.selectedProperty().setValue(false);
			docCamCheckBox.selectedProperty().setValue(false);
		}
	}

	private void applyButtonAction() {
		Room selected = roomListView.getSelectionModel().getSelectedItem();
		assert selected != null;
		RoomFeatures editedFeatures;
		try {
			editedFeatures = new RoomFeatures(
					projectorsChoiceBox.getValue(), pcPoolCheckBox.isSelected(),
					teacherPcCheckBox.isSelected(), docCamCheckBox.isSelected());
		} catch (WctttModelException e) {
			throw new WctttGuiFatalException("Implementation error, input of " +
					"illegal room feature values was permitted", e);
		}

		if (internalCheckBox.selectedProperty().getValue()) {
			try {
				if (selected instanceof InternalRoom) {
					InternalRoom selectedInternal = (InternalRoom) selected;
					getModel().updateInternalRoomData(selectedInternal,
							nameField.getText(),
							Integer.parseInt(capacityField.getText()),
							holderChoiceBox.getValue(), editedFeatures);
				} else {
					ExternalRoom selectedExternal = (ExternalRoom) selected;
					InternalRoom newRoom = new InternalRoom(
							selectedExternal.getId(), nameField.getText(),
							Integer.parseInt(capacityField.getText()),
							holderChoiceBox.getValue(), editedFeatures);
					getModel().removeExternalRoom(selectedExternal);
					getModel().addInternalRoom(newRoom);
				}
			} catch (NumberFormatException e) {
				Util.errorAlert("Problem with editing the rooms", "The room " +
						"capacity must be an integer >= " +
						ValidationHelper.ROOM_CAPACITY_MIN);
			} catch (WctttModelException e) {
				Util.errorAlert("Problem with editing the rooms",
						e.getMessage());
			}
		} else {
			if (selected instanceof InternalRoom) {
				InternalRoom selectedInternal = (InternalRoom) selected;
				ExternalRoom newRoom = new ExternalRoom(
						selectedInternal.getId(), nameField.getText());
				try {
					getModel().removeInternalRoom(selectedInternal);
					getModel().addExternalRoom(newRoom);
				} catch (WctttModelException e) {
					Util.errorAlert("Problem with editing the rooms",
							e.getMessage());
				}
			} else {
				try {
					getModel().updateExternalRoomData((ExternalRoom) selected,
							nameField.getText());
				} catch (WctttModelException e) {
					throw new WctttGuiFatalException("Implementation error, " +
							"session was not added to the semester", e);
				}
			}
		}
	}

	@Override
	public void setup(Stage stage, Model model, MainController mainController) {
		super.setup(stage, model, mainController);
		getModel().subscribeSemesterChanges(this);
		updateRoomList(true);

		holderChoiceBox.getItems().add(null);
		holderChoiceBox.getItems().addAll(getModel().getChairs());
		projectorsChoiceBox.setItems(FXCollections.observableList(
				IntStream.range(ValidationHelper.PROJECTORS_MIN, 3).
						boxed().collect(Collectors.toList())));
	}

	private void updateRoomList(boolean fullReloadNecessary) {
		if (fullReloadNecessary) {
			@SuppressWarnings("unchecked")
			List<Room> internalRooms = (List<Room>) (List<? extends Room>)
					getModel().getInternalRooms();
			@SuppressWarnings("unchecked")
			List<Room> externalRooms = (List<Room>) (List<? extends Room>)
					getModel().getExternalRooms();
			Platform.runLater(() -> {
				roomListView.getItems().clear();
				roomListView.getItems().addAll(internalRooms);
				roomListView.getItems().addAll(externalRooms);
			});
		} else {
			Platform.runLater(() -> roomListView.refresh());
		}
	}

	@Override
	public void onNext(Boolean item) {
		updateRoomList(item);
		getSubscription().request(1);
	}
}
