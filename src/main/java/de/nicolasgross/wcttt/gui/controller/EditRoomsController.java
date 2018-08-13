package de.nicolasgross.wcttt.gui.controller;

import de.nicolasgross.wcttt.gui.model.Model;
import de.nicolasgross.wcttt.lib.model.*;
import javafx.application.Platform;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.util.List;

public class EditRoomsController extends Controller {

	@FXML
	private ListView<Room> roomListView;
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
		addRoomButton.setOnAction(event -> {
			try {
				InternalRoom newRoom = new InternalRoom();
				getModel().getSemester().addInternalRoom(newRoom);
				Platform.runLater(() -> {
					roomListView.getSelectionModel().select(newRoom);
					roomListView.getFocusModel().focus(roomListView.
							getSelectionModel().getSelectedIndex());
					roomListView.requestFocus();
				});
			} catch (WctttModelException e) {
				e.printStackTrace();
				// TODO
			}
		});
	}

	@Override
	public void setup(Stage stage, Model model, MainController mainController) {
		super.setup(stage, model, mainController);
		mergeInternalAndExternalRooms();

		getModel().getInternalRooms().addListener(
				(ListChangeListener<InternalRoom>) c ->
						mergeInternalAndExternalRooms());

		getModel().getExternalRooms().addListener(
				(ListChangeListener<ExternalRoom>) c ->
						mergeInternalAndExternalRooms());
	}

	private void mergeInternalAndExternalRooms() {
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
	}
}
