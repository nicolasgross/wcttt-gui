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

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class EditRoomsController extends SubscriberController<Semester> {

	private static final List<Integer> PROJECTOR_CHOICE = Arrays.asList(0, 1, 2);

	@FXML
	private ListView<Room> roomListView;
	@FXML
	private VBox editBox;
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
					if (newValue == null) {
						editBox.disableProperty().setValue(true);
						nameField.setText("");
					} else if (oldValue == null) {
						editBox.disableProperty().setValue(false);
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

		applyButton.setOnAction(event -> {
			Room selected = roomListView.getSelectionModel().getSelectedItem();

			if (selected instanceof InternalRoom) {
				try {
					getModel().updateInternalRoomData((InternalRoom) selected, nameField.getText(),
							((InternalRoom) selected).getCapacity(), ((InternalRoom) selected).getHolder().orElse(null), ((InternalRoom) selected).getFeatures());
				} catch (WctttModelException e) {
					e.printStackTrace();
				}
			} else {

			}
		});
	}

	@Override
	public void setup(Stage stage, Model model, MainController mainController) {
		super.setup(stage, model, mainController);
		getModel().subscribeSemesterChanges(this);
		mergeAndShowInternalAndExternalRooms();

		holderChoiceBox.getItems().add(null);
		holderChoiceBox.getItems().addAll(getModel().getChairs());
		projectorsChoiceBox.setItems(
				FXCollections.observableList(PROJECTOR_CHOICE));
	}

	private void mergeAndShowInternalAndExternalRooms() {
		@SuppressWarnings("unchecked")
		List<Room> internalRooms = (List<Room>) (List<? extends Room>)
				getModel().getInternalRooms();
		@SuppressWarnings("unchecked")
		List<Room> externalRooms = (List<Room>) (List<? extends Room>)
				getModel().getExternalRooms();
		if (internalRooms.size() + externalRooms.size() ==
				roomListView.getItems().size()) {
			Platform.runLater(() -> roomListView.refresh());
		} else {
			Platform.runLater(() -> {
				roomListView.getItems().clear();
				roomListView.getItems().addAll(internalRooms);
				roomListView.getItems().addAll(externalRooms);
			});
		}
	}

	@Override
	public void onNext(Semester item) {
		mergeAndShowInternalAndExternalRooms();
		getSubscription().request(1);
	}
}
