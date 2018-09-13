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
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import wcttt.gui.model.Model;
import wcttt.lib.model.*;

import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Controller for the view that is concerned with editing the courses.
 */
public class EditCoursesController extends SubscriberController<Boolean> {

	@FXML
	private EditCourseSessionsController editSessionsController;

	@FXML
	private BorderPane rootPane;
	@FXML
	private VBox editCourseVBox;
	@FXML
	private TreeView<TreeViewItemWrapper<?>> coursesTreeView;
	@FXML
	private Button addCourseButton;
	@FXML
	private TextField nameField;
	@FXML
	private TextField abbreviationField;
	@FXML
	private ComboBox<Chair> chairChoiceBox;
	@FXML
	private ChoiceBox<CourseLevel> courseLevelChoiceBox;
	@FXML
	private ChoiceBox<Integer> minNumOfDaysChoiceBox;
	@FXML
	private Button addLectureButton;
	@FXML
	private Button addPracticalButton;
	@FXML
	private Button applyButton;

	@FXML
	protected void initialize() {
		// edit session vbox is stored on the right
		rootPane.setRight(null);

		coursesTreeView.setRoot(new TreeItem<>(
				new TreeViewItemWrapper<>("root")));
		coursesTreeView.setShowRoot(false);

		coursesTreeView.getSelectionModel().selectedItemProperty().addListener(
				(observable, oldValue, newValue) -> {
					if (newValue == null ||
							(!(newValue.getValue().getItem() instanceof Course) &&
							!(newValue.getValue().getItem() instanceof Session))) {
						updateCourseEditVBox(null);
						rootPane.setCenter(editCourseVBox);
						rootPane.getCenter().disableProperty().setValue(true);
					} else if (newValue.getValue().getItem() instanceof Course) {
						updateCourseEditVBox((Course) newValue.getValue().getItem());
						rootPane.setCenter(editCourseVBox);
						rootPane.getCenter().disableProperty().setValue(false);
					} else {
						boolean isLecture = newValue.getParent().getValue().
								getItem().equals("Lectures");
						rootPane.setCenter(editSessionsController.
								getEditSessionVBox((Session) newValue.
										getValue().getItem(), isLecture));
						rootPane.getCenter().disableProperty().setValue(false);
					}
				});

		ContextMenu contextMenu = new ContextMenu();
		MenuItem deleteMenuItem = new MenuItem("Delete");
		deleteMenuItem.setOnAction(event -> contextDeleteAction());
		contextMenu.getItems().add(deleteMenuItem);

		coursesTreeView.setCellFactory(param -> {
			TreeCell<TreeViewItemWrapper<?>> cell = new TreeCell<>();

			cell.textProperty().bind(Bindings.when(cell.emptyProperty()).
					then("").otherwise(cell.itemProperty().asString()));

			cell.itemProperty().addListener((observable, oldValue, newValue) -> {
				if (newValue == null || (!(newValue.getItem() instanceof Course)
						&& !(newValue.getItem() instanceof Session))) {
					cell.setContextMenu(null);
				} else {
					cell.setContextMenu(contextMenu);
				}
			});

			return cell;
		});

		addCourseButton.setOnAction(event -> addCourseAction());

		addLectureButton.setOnAction(event -> addLectureAction());

		addPracticalButton.setOnAction(event -> addPracticalAction());

		applyButton.setOnAction(event -> applyButtonAction());
	}

	private void addCourseAction() {
		try {
			getModel().addCourse(new Course());
		} catch (WctttModelException e) {
			Util.errorAlert("Problem with editing the courses",
					e.getMessage());
		}
	}

	private void addLectureAction() {
		Object selected = coursesTreeView.getSelectionModel().
				getSelectedItem().getValue().getItem();
		assert selected instanceof Course;
		try {
			getModel().addCourseLecture(new InternalSession(),
					(Course) selected);
		} catch (WctttModelException e) {
			Util.errorAlert("Problem with editing the course",
					e.getMessage());
		}
	}

	private void addPracticalAction() {
		Object selected = coursesTreeView.getSelectionModel().
				getSelectedItem().getValue().getItem();
		assert selected instanceof Course;
		try {
			getModel().addCoursePractical(new InternalSession(),
					(Course) selected);
		} catch (WctttModelException e) {
			Util.errorAlert("Problem with editing the course",
					e.getMessage());
		}
	}

	private void contextDeleteAction() {
		TreeItem<TreeViewItemWrapper<?>> selection =
				coursesTreeView.getSelectionModel().getSelectedItem();
		boolean confirmed;
		if (selection.getValue().getItem() instanceof Course) {
			confirmed = Util.confirmationAlert("Confirm deletion of " +
					"course", "Are you sure you want to delete the " +
					"selected course?");
		} else {
			confirmed = Util.confirmationAlert("Confirm deletion of " +
					"session", "Are you sure you want to delete the " +
					"selected session?");
		}
		if (confirmed) {
			try {
				if (selection.getValue().getItem() instanceof Course) {
					getModel().removeCourse(
							(Course) selection.getValue().getItem());
				} else {
					if (selection.getParent().getValue().getItem().
							equals("Lectures")) {
						getModel().removeCourseLecture(
								(Session) selection.getValue().getItem());
					} else {
						getModel().removeCoursePractical(
								(Session) selection.getValue().getItem());
					}
				}
			} catch (WctttModelException e) {
				Util.errorAlert("Problem with editing the courses",
						e.getMessage());
			}
		}
	}

	private void applyButtonAction() {
		TreeItem<TreeViewItemWrapper<?>> selection =
				coursesTreeView.getSelectionModel().getSelectedItem();
		assert selection.getValue().getItem() instanceof Course;
		Course course = (Course) selection.getValue().getItem();
		try {
			getModel().updateCourseData(course, nameField.getText(),
					abbreviationField.getText(), chairChoiceBox.getValue(),
					courseLevelChoiceBox.getValue(),
					minNumOfDaysChoiceBox.getValue());
		} catch (WctttModelException e) {
			Util.errorAlert("Problem with editing the course",
					e.getMessage());
		}
	}

	private void updateCourseEditVBox(Course course) {
		if (course == null) {
			nameField.setText("");
			abbreviationField.setText("");
			chairChoiceBox.setValue(null);
			courseLevelChoiceBox.setValue(null);
			minNumOfDaysChoiceBox.setValue(null);
		} else {
			nameField.setText(course.getName());
			abbreviationField.setText(course.getAbbreviation());
			chairChoiceBox.setValue(course.getChair());
			courseLevelChoiceBox.setValue(course.getCourseLevel());
			minNumOfDaysChoiceBox.setValue(course.getMinNumberOfDays());
		}
	}

	private List<TreeItem<TreeViewItemWrapper<?>>> createCourseTree() {
		List<TreeItem<TreeViewItemWrapper<?>>> courses = new LinkedList<>();
		for (Course course : getModel().getCourses()) {
			TreeViewItemWrapper<?> courseWrapper =
					new TreeViewItemWrapper<>(course);
			TreeItem<TreeViewItemWrapper<?>> courseItem =
					new TreeItem<>(courseWrapper);

			TreeViewItemWrapper<?> lecturesWrapper =
					new TreeViewItemWrapper<>("Lectures");
			TreeItem<TreeViewItemWrapper<?>> lecturesItem =
					new TreeItem<>(lecturesWrapper);
			for (Session session : course.getLectures()) {
				TreeViewItemWrapper<?> sessionWrapper =
						new TreeViewItemWrapper<>(session);
				TreeItem<TreeViewItemWrapper<?>> sessionItem =
						new TreeItem<>(sessionWrapper);
				lecturesItem.getChildren().add(sessionItem);
			}

			TreeViewItemWrapper<?> practicalsWrapper =
					new TreeViewItemWrapper<>("Practicals");
			TreeItem<TreeViewItemWrapper<?>> practicalsItem =
					new TreeItem<>(practicalsWrapper);
			for (Session session : course.getPracticals()) {
				TreeViewItemWrapper<?> sessionWrapper =
						new TreeViewItemWrapper<>(session);
				TreeItem<TreeViewItemWrapper<?>> sessionItem =
						new TreeItem<>(sessionWrapper);
				practicalsItem.getChildren().add(sessionItem);
			}

			courseItem.getChildren().add(lecturesItem);
			courseItem.getChildren().add(practicalsItem);
			courses.add(courseItem);
		}

		// keep expanded state of tree items
		for (TreeItem<TreeViewItemWrapper<?>> oldItem :
				coursesTreeView.getRoot().getChildren()) {
			if (oldItem.isExpanded()) {
				for (TreeItem<TreeViewItemWrapper<?>> newItem : courses) {
					if (newItem.getValue().getItem().equals(
							oldItem.getValue().getItem())) {
						newItem.setExpanded(true);
						if (oldItem.getChildren().get(0).isExpanded()) {
							newItem.getChildren().get(0).setExpanded(true);
						}
						if (oldItem.getChildren().get(1).isExpanded()) {
							newItem.getChildren().get(1).setExpanded(true);
						}
					}
				}
			}
		}
		return courses;
	}

	private void updateCoursesTreeView(boolean fullReloadNecessary) {
		if (fullReloadNecessary) {
			List<TreeItem<TreeViewItemWrapper<?>>> courses = createCourseTree();
			Platform.runLater(() -> {
				coursesTreeView.getRoot().getChildren().clear();
				coursesTreeView.getRoot().getChildren().addAll(courses);
				coursesTreeView.getRoot().getChildren().sort(
						Comparator.comparing(t -> t.getValue().toString()));
			});
		} else {
			Platform.runLater(() -> {
				coursesTreeView.refresh();
				coursesTreeView.getRoot().getChildren().sort(
						Comparator.comparing(t -> t.getValue().toString()));
			});
		}
	}

	@Override
	public void setup(Stage stage, HostServices hostServices,
	                  MainController mainController, Model model) {
		super.setup(stage, hostServices, mainController, model);
		getModel().subscribeSemesterChanges(this);
		editSessionsController.setup(stage, hostServices,
			mainController, model);
		chairChoiceBox.setItems(getModel().getChairs());
		courseLevelChoiceBox.setItems(FXCollections.observableList(
				Arrays.asList(CourseLevel.values())));
		minNumOfDaysChoiceBox.setItems(FXCollections.observableList(
				IntStream.range(1, getModel().getDaysPerWeek() + 1).boxed().
						collect(Collectors.toList())));
		updateCoursesTreeView(true);
	}

	@Override
	public void onNext(Boolean item) {
		updateCoursesTreeView(item);
		getSubscription().request(1);
	}
}
