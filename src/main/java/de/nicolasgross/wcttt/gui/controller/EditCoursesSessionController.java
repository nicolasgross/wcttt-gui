package de.nicolasgross.wcttt.gui.controller;

import de.nicolasgross.wcttt.lib.model.ExternalRoom;
import de.nicolasgross.wcttt.lib.model.Period;
import de.nicolasgross.wcttt.lib.model.Session;
import de.nicolasgross.wcttt.lib.model.Teacher;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

public class EditCoursesSessionController extends Controller {

	@FXML
	private VBox editSessionVBox;
	@FXML
	private TextField nameField;
	@FXML
	private ChoiceBox<Teacher> teacherChoiceBox;
	@FXML
	private CheckBox doubleSessionCheckBox;
	@FXML
	private ChoiceBox<Period> preAssignmentChoiceBox;
	@FXML
	private CheckBox internalCheckBox;
	@FXML
	private ChoiceBox<ExternalRoom> roomChoiceBox;
	@FXML
	private TextField studentsTextField;
	@FXML
	private Accordion requirementsAccordion;
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
		roomChoiceBox.disableProperty().bind(internalCheckBox.selectedProperty());
		studentsTextField.disableProperty().bind(internalCheckBox.selectedProperty().not());
		requirementsAccordion.disableProperty().bind(internalCheckBox.selectedProperty().not());

	}

	public VBox getEditSessionVBox(Session selected) {
		return editSessionVBox;
	}
}
