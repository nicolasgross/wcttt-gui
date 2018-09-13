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
import wcttt.lib.model.Course;
import wcttt.lib.model.Curriculum;
import wcttt.lib.model.Teacher;
import wcttt.lib.model.WctttModelException;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.util.LinkedList;
import java.util.List;

/**
 * Controller for the view that is concerned with editing the curricula.
 */
public class EditCurriculaController extends SubscriberController<Boolean> {

	@FXML
	private BorderPane rootPane;
	@FXML
	private TextField nameField;
	@FXML
	private ListView<Curriculum> curriculaListView;
	@FXML
	private Button addCurriculumButton;
	@FXML
	private ListView<Course> addedCoursesListView;
	@FXML
	private ListView<Course> allCoursesListView;
	@FXML
	private Button addCourseButton;
	@FXML
	private Button removeCourseButton;
	@FXML
	private Button applyButton;

	private Teacher selectedTeacher;

	@FXML
	protected void initialize() {
		curriculaListView.getSelectionModel().setSelectionMode(
				SelectionMode.MULTIPLE);

		curriculaListView.getSelectionModel().selectedItemProperty().
				addListener((observable, oldValue, newValue) -> {
					if (newValue == null) {
						rootPane.getCenter().disableProperty().setValue(true);
					} else {
						rootPane.getCenter().disableProperty().setValue(false);
					}
					updateEditVBox(newValue);
				});

		ContextMenu contextMenu = new ContextMenu();
		MenuItem deleteMenuItem = new MenuItem("Delete");
		deleteMenuItem.setOnAction(event -> contextDeleteAction());
		contextMenu.getItems().add(deleteMenuItem);

		curriculaListView.setCellFactory(param -> {
			ListCell<Curriculum> cell = new ListCell<>();

			cell.textProperty().bind(Bindings.when(cell.emptyProperty()).
					then("").otherwise(cell.itemProperty().asString()));

			cell.contextMenuProperty().bind(
					Bindings.when(cell.emptyProperty())
							.then((ContextMenu) null)
							.otherwise(contextMenu)
			);
			return cell;
		});

		addCurriculumButton.setOnAction(event -> addCurriculumAction());

		addCourseButton.setOnAction(event -> addCourseAction());
		removeCourseButton.setOnAction(event -> removeCourseAction());

		applyButton.setOnAction(event -> applyButtonAction());
	}

	private void contextDeleteAction() {
		List<Curriculum> selection =
				curriculaListView.getSelectionModel().getSelectedItems();
		boolean confirmed = Util.confirmationAlert("Confirm deletion of " +
				"curricula", "Are you sure you want to delete the " +
				"selected curricul" + (selection.size() == 1 ? "um" : "a")
				+ "?");
		if (confirmed) {
			for (Curriculum curriculum : new LinkedList<>(selection)) {
				try {
					getModel().removeCurriculum(curriculum);
				} catch (WctttModelException e) {
					Util.errorAlert("Problem with editing the curricula",
							e.getMessage());
				}
			}
		}
	}

	private void addCurriculumAction() {
		try {
			getModel().addCurriculum(new Curriculum());
		} catch (WctttModelException e) {
			Util.errorAlert("Problem with editing the curricula",
					e.getMessage());
		}
	}

	private void addCourseAction() {
		Course selected = allCoursesListView.getSelectionModel().
				getSelectedItem();
		if (selected == null) {
			return;
		}
		allCoursesListView.getItems().remove(selected);
		addedCoursesListView.getItems().add(selected);
	}

	private void removeCourseAction() {
		Course selected = addedCoursesListView.getSelectionModel().
				getSelectedItem();
		if (selected == null) {
			return;
		}
		addedCoursesListView.getItems().remove(selected);
		allCoursesListView.getItems().add(selected);
	}

	private void applyButtonAction() {
		Curriculum selected = curriculaListView.getSelectionModel().
				getSelectedItem();
		assert selected != null;
		try {
			getModel().updateCurriculumData(selected, nameField.getText(),
					new LinkedList<>(addedCoursesListView.getItems()));
		} catch (WctttModelException e) {
			Util.errorAlert("Problem with editing the curriculum",
					e.getMessage());
		}
	}

	@Override
	public void setup(Stage stage, HostServices hostServices,
	                  MainController mainController, Model model) {
		super.setup(stage, hostServices, mainController, model);
		getModel().subscribeSemesterChanges(this);
		updateEditVBox(null);
		curriculaListView.setItems(getModel().getCurricula());
	}

	private void updateEditVBox(Curriculum selected) {
		if (selected == null) {
			nameField.setText("");
			addedCoursesListView.getItems().clear();
			allCoursesListView.getItems().clear();
			allCoursesListView.getItems().addAll(getModel().getCourses());
		} else {
			nameField.setText(selected.getName());
			addedCoursesListView.getItems().clear();
			allCoursesListView.getItems().clear();
			for (Course course : getModel().getCourses()) {
				if (selected.getCourses().contains(course)) {
					addedCoursesListView.getItems().add(course);
				} else {
					allCoursesListView.getItems().add(course);
				}
			}
		}
	}

	@Override
	public void onNext(Boolean item) {
		// a full reload happens if a curriculum was added/removed and can be
		// ignored because an ObservableList is used for the curricula list
		if (!item) {
			Platform.runLater(() -> curriculaListView.refresh());
		}
		getSubscription().request(1);
	}
}
