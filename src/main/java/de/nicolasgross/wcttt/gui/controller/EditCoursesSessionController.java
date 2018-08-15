package de.nicolasgross.wcttt.gui.controller;

import de.nicolasgross.wcttt.gui.WctttGuiFatalException;
import de.nicolasgross.wcttt.gui.model.Model;
import de.nicolasgross.wcttt.lib.model.*;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

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
		roomChoiceBox.disableProperty().bind(
				internalCheckBox.selectedProperty());
		studentsTextField.disableProperty().bind(
				internalCheckBox.selectedProperty().not());
		requirementsAccordion.disableProperty().bind(
				internalCheckBox.selectedProperty().not());

		applyButton.setOnAction(event -> applyButtonAction());
	}

	private void applyButtonAction() {
		// TODO
	}

	@Override
	public void setup(Stage stage, Model model, MainController mainController) {
		super.setup(stage, model, mainController);

		teacherChoiceBox.setItems(getModel().getTeachers());
		List<Period> periods = new LinkedList<>();
		periods.add(null);
		for (int i = 0; i < getModel().getDaysPerWeek(); i++) {
			for (int j = 0; j < getModel().getTimeSlotsPerDay(); j++) {
				try {
					periods.add(new Period(i + 1, j + 1));
				} catch (WctttModelException e) {
					throw new WctttGuiFatalException("Implementation error, " +
							"created a period with invalid parameters", e);
				}
			}
		}
		preAssignmentChoiceBox.setItems(FXCollections.observableList(periods));
		roomChoiceBox.setItems(getModel().getExternalRooms());
		projectorsChoiceBox.setItems(FXCollections.observableList(
				IntStream.range(ValidationHelper.PROJECTORS_MIN, 3).
						boxed().collect(Collectors.toList())));
	}

	VBox getEditSessionVBox(Session selected) {
		Platform.runLater(() -> updateSessionEditVBox(selected));
		return editSessionVBox;
	}

	private void updateSessionEditVBox(Session selected) {
		if (selected == null) {
			nameField.setText("");
			teacherChoiceBox.setValue(null);
			doubleSessionCheckBox.setSelected(false);
			preAssignmentChoiceBox.setValue(null);
			internalCheckBox.setSelected(true);
			roomChoiceBox.setValue(null);
			studentsTextField.setText("");
			projectorsChoiceBox.setValue(null);
			pcPoolCheckBox.setSelected(false);
			teacherPcCheckBox.setSelected(false);
			docCamCheckBox.setSelected(false);
		} else {
			nameField.setText(selected.getName());
			teacherChoiceBox.setValue(selected.getTeacher());
			doubleSessionCheckBox.setSelected(selected.isDoubleSession());
			preAssignmentChoiceBox.setValue(
					selected.getPreAssignment().orElse(null));
			if (selected instanceof InternalSession) {
				InternalSession tmp = (InternalSession) selected;
				internalCheckBox.setSelected(true);
				roomChoiceBox.setValue(null);
				studentsTextField.setText(String.valueOf(tmp.getStudents()));
				projectorsChoiceBox.setValue(
						tmp.getRoomRequirements().getProjectors());
				pcPoolCheckBox.setSelected(
						tmp.getRoomRequirements().isPcPool());
				teacherPcCheckBox.setSelected(
						tmp.getRoomRequirements().hasTeacherPc());
				docCamCheckBox.setSelected(
						tmp.getRoomRequirements().hasDocCam());
			} else {
				ExternalSession tmp = (ExternalSession) selected;
				internalCheckBox.setSelected(false);
				roomChoiceBox.setValue(tmp.getRoom());
				studentsTextField.setText("");
				projectorsChoiceBox.setValue(null);
				pcPoolCheckBox.setSelected(false);
				teacherPcCheckBox.setSelected(false);
				docCamCheckBox.setSelected(false);
			}
		}
	}
}
