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
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import wcttt.lib.model.*;

import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Controller for the view that is concerned with editing the sessions of a
 * course.
 */
public class EditCourseSessionsController extends Controller {

	@FXML
	private VBox editSessionVBox;
	@FXML
	private TextField nameField;
	@FXML
	private ComboBox<Teacher> teacherChoiceBox;
	@FXML
	private CheckBox doubleSessionCheckBox;
	@FXML
	private ComboBox<Period> preAssignmentChoiceBox;
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

	private Session selectedSession;
	private boolean isLecture;

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
		RoomFeatures editedRequirements;
		assert selectedSession != null;
		try {
			editedRequirements = new RoomFeatures(
					projectorsChoiceBox.getValue(), pcPoolCheckBox.isSelected(),
					teacherPcCheckBox.isSelected(), docCamCheckBox.isSelected());
		} catch (WctttModelException e) {
			throw new WctttGuiFatalException("Implementation error, input of " +
					"illegal room requirement values was permitted", e);
		}

		if (internalCheckBox.selectedProperty().getValue()) {
			try {
				if (selectedSession instanceof InternalSession) {
					InternalSession session = (InternalSession) selectedSession;
					getModel().updateInternalSessionData(session,
							nameField.getText(), teacherChoiceBox.getValue(),
							doubleSessionCheckBox.isSelected(),
							preAssignmentChoiceBox.getValue(),
							Integer.parseInt(studentsTextField.getText()),
							editedRequirements);
				} else {
					ExternalSession session = (ExternalSession) selectedSession;
					InternalSession internalVariant = new InternalSession(
							session.getId(), nameField.getText(),
							teacherChoiceBox.getValue(), session.getCourse(),
							doubleSessionCheckBox.isSelected(),
							preAssignmentChoiceBox.getValue(),
							Integer.parseInt(studentsTextField.getText()),
							editedRequirements);
					handleSessionTypeChange(session, internalVariant);
				}
			} catch (NumberFormatException e) {
				Util.errorAlert("Problem with editing the session", "The " +
						"number of students must be an integer >= " +
						ValidationHelper.STUDENTS_MIN);
			} catch (WctttModelException e) {
				Util.errorAlert("Problem with editing the sessions",
						e.getMessage());
			}
		} else {
			try {
				if (selectedSession instanceof InternalSession) {
					InternalSession session = (InternalSession) selectedSession;
					ExternalSession externalVariant = new ExternalSession(
							session.getId(), nameField.getText(),
							teacherChoiceBox.getValue(), session.getCourse(),
							doubleSessionCheckBox.isSelected(),
							preAssignmentChoiceBox.getValue(),
							roomChoiceBox.getValue());
					handleSessionTypeChange(session, externalVariant);
				} else {
					ExternalSession session = (ExternalSession) selectedSession;
					getModel().updateExternalSessionData(session,
							nameField.getText(), teacherChoiceBox.getValue(),
							doubleSessionCheckBox.isSelected(),
							preAssignmentChoiceBox.getValue(),
							roomChoiceBox.getValue());
				}
			} catch (WctttModelException e) {
				Util.errorAlert("Problem with editing the sessions",
						e.getMessage());
			}
		}
	}

	private void handleSessionTypeChange(Session oldSession, Session newSession)
			throws WctttModelException {
		if (isLecture) {
			getModel().removeCourseLecture(oldSession);
			getModel().addCourseLecture(newSession,
					newSession.getCourse());
		} else {
			getModel().removeCoursePractical(oldSession);
			getModel().addCoursePractical(newSession,
					newSession.getCourse());
		}
	}

	@Override
	public void setup(Stage stage, HostServices hostServices, MainController mainController, Model model) {
		super.setup(stage, hostServices, mainController, model);
		teacherChoiceBox.setItems(getModel().getTeachers());
		preAssignmentChoiceBox.getItems().add(null);
		preAssignmentChoiceBox.getItems().addAll(getModel().getPeriods());
		roomChoiceBox.setItems(getModel().getExternalRooms());
		projectorsChoiceBox.setItems(FXCollections.observableList(
				IntStream.range(ValidationHelper.PROJECTORS_MIN, 3).
						boxed().collect(Collectors.toList())));
	}

	VBox getEditSessionVBox(Session selected, boolean isLecture) {
		this.selectedSession = selected;
		this.isLecture = isLecture;
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
			projectorsChoiceBox.setValue(0);
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
				projectorsChoiceBox.setValue(0);
				pcPoolCheckBox.setSelected(false);
				teacherPcCheckBox.setSelected(false);
				docCamCheckBox.setSelected(false);
			}
		}
	}
}
