package de.nicolasgross.wcttt.gui.controller;

import de.nicolasgross.wcttt.lib.model.Chair;
import de.nicolasgross.wcttt.lib.model.Room;
import javafx.fxml.FXML;
import javafx.scene.control.*;

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
}
