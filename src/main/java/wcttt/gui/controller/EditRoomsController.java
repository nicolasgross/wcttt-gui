/*
 * WCT³ (WIAI Course Timetabling Tool) is a software that strives to automate
 * the timetabling process at the WIAI faculty of the University of Bamberg.
 *
 * WCT³-GUI comprises functionality to view generated timetables, edit semester
 * data and to generate new timetables.
 *
 * Copyright (C) 2018 Nicolas Gross
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package wcttt.gui.controller;

import javafx.application.HostServices;
import wcttt.gui.WctttGuiFatalException;
import wcttt.gui.model.Model;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import wcttt.lib.model.*;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Controller for the view that is concerned with editing the rooms.
 */
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
		deleteMenuItem.setOnAction(event -> contextDeleteAction());
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
		featuresAccordion.disableProperty().bind(
				internalCheckBox.selectedProperty().not());

		addRoomButton.setOnAction(event -> {
			try {
				getModel().addInternalRoom(new InternalRoom());
			} catch (WctttModelException e) {
				Util.errorAlert("Problem with editing the rooms",
						e.getMessage());
			}
		});

		applyButton.setOnAction(event -> applyButtonAction());
	}

	private void contextDeleteAction() {
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
					Util.errorAlert("Problem with editing the room",
							e.getMessage());
				}
			}
		}
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
							editedFeatures);
				} else {
					ExternalRoom selectedExternal = (ExternalRoom) selected;
					InternalRoom newRoom = new InternalRoom(
							selectedExternal.getId(), nameField.getText(),
							Integer.parseInt(capacityField.getText()),
							editedFeatures);
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
	public void setup(Stage stage, HostServices hostServices,
	                  MainController mainController, Model model) {
		super.setup(stage, hostServices, mainController, model);
		getModel().subscribeSemesterChanges(this);
		updateRoomList(true);

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
