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
import wcttt.gui.model.Model;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.stage.Stage;
import wcttt.lib.model.Chair;
import wcttt.lib.model.Course;
import wcttt.lib.model.Curriculum;
import wcttt.lib.model.Teacher;

/**
 * Controller for the filter functionality in the side menu.
 */
public class MainFiltersController extends SubscriberController<Boolean> {

	@FXML
	private ComboBox<Teacher> teacherSelection;
	@FXML
	private ComboBox<Chair> chairSelection;
	@FXML
	private ComboBox<Course> courseSelection;
	@FXML
	private ComboBox<Curriculum> curriculumSelection;
	@FXML
	private Button resetButton;
	@FXML
	private Button filterButton;

	@FXML
	protected void initialize() {
		resetButton.setOnAction(event -> {
			teacherSelection.getSelectionModel().clearSelection();
			chairSelection.getSelectionModel().clearSelection();
			courseSelection.getSelectionModel().clearSelection();
			curriculumSelection.getSelectionModel().clearSelection();
			getMainController().getTimetableTableController().filter(
					null, null, null, null);
		});

		filterButton.setOnAction(event -> {
			getMainController().getTimetableTableController().filter(
					teacherSelection.getValue(), chairSelection.getValue(),
					courseSelection.getValue(), curriculumSelection.getValue());
		});

	}

	@Override
	public void setup(Stage stage, HostServices hostServices,
	                  MainController mainController, Model model) {
		super.setup(stage, hostServices, mainController, model);
		model.subscribeSemesterChanges(this);
		updateGui();
	}

	@Override
	public void onNext(Boolean item) {
		updateGui();
		getSubscription().request(1);
	}

	private void updateGui() {
		Platform.runLater(() -> {
			teacherSelection.getItems().clear();
			teacherSelection.getItems().add(null);
			teacherSelection.getItems().addAll(getModel().getTeachers());
			chairSelection.getItems().clear();
			chairSelection.getItems().add(null);
			chairSelection.getItems().addAll(getModel().getChairs());
			courseSelection.getItems().clear();
			courseSelection.getItems().add(null);
			courseSelection.getItems().addAll(getModel().getCourses());
			curriculumSelection.getItems().clear();
			curriculumSelection.getItems().add(null);
			curriculumSelection.getItems().addAll(getModel().getCurricula());
		});
	}
}
