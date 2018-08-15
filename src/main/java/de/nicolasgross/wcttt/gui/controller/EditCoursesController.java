package de.nicolasgross.wcttt.gui.controller;

import de.nicolasgross.wcttt.gui.model.Model;
import de.nicolasgross.wcttt.lib.model.Course;
import de.nicolasgross.wcttt.lib.model.Semester;
import de.nicolasgross.wcttt.lib.model.Session;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.LinkedList;
import java.util.List;

public class EditCoursesController extends SubscriberController<Semester> {

	@FXML
	private EditCoursesSessionController editSessionController;

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
	private ChoiceBox chairChoiceBox;
	@FXML
	private ChoiceBox courseLevelChoiceBox;
	@FXML
	private ChoiceBox minNumOfDaysChoiceBox;
	@FXML
	private Button addLectureButton;
	@FXML
	private Button addPracticalButton;
	@FXML
	private Button applyButton;

	private boolean fullReloadNecessary;

	@FXML
	protected void initialize() {
		rootPane.setRight(null);

		coursesTreeView.setRoot(new TreeItem<>(
				new TreeViewItemWrapper<>("root")));
		coursesTreeView.setShowRoot(false);
	}

	private List<TreeItem<TreeViewItemWrapper<?>>> createCourseTree() {
		List<TreeItem<TreeViewItemWrapper<?>>> courses = new LinkedList<>();
		for (Course course : getModel().getCourses()) {
			TreeViewItemWrapper<?> courseWrapper = new TreeViewItemWrapper<>(course);
			TreeItem<TreeViewItemWrapper<?>> courseItem = new TreeItem<>(courseWrapper);

			TreeViewItemWrapper<?> lecturesWrapper = new TreeViewItemWrapper<>("Lectures");
			TreeItem<TreeViewItemWrapper<?>> lecturesItem = new TreeItem<>(lecturesWrapper);
			for (Session session : course.getLectures()) {
				TreeViewItemWrapper<?> sessionWrapper = new TreeViewItemWrapper<>(session);
				TreeItem<TreeViewItemWrapper<?>> sessionItem = new TreeItem<>(sessionWrapper);
				lecturesItem.getChildren().add(sessionItem);
			}

			TreeViewItemWrapper<?> practicalsWrapper = new TreeViewItemWrapper<>("Practicals");
			TreeItem<TreeViewItemWrapper<?>> practicalsItem = new TreeItem<>(practicalsWrapper);
			for (Session session : course.getPracticals()) {
				TreeViewItemWrapper<?> sessionWrapper = new TreeViewItemWrapper<>(session);
				TreeItem<TreeViewItemWrapper<?>> sessionItem = new TreeItem<>(sessionWrapper);
				practicalsItem.getChildren().add(sessionItem);
			}

			courseItem.getChildren().add(lecturesItem);
			courseItem.getChildren().add(practicalsItem);
			courses.add(courseItem);
		}
		return courses;
	}

	private void updateCoursesTreeView() {
		if (fullReloadNecessary) {
			List<TreeItem<TreeViewItemWrapper<?>>> courses = createCourseTree();
			Platform.runLater(() -> {
				coursesTreeView.getRoot().getChildren().clear();
				coursesTreeView.getRoot().getChildren().addAll(courses);
			});
			fullReloadNecessary = false;
		} else {
			Platform.runLater(() -> coursesTreeView.refresh());
		}
	}

	@Override
	public void setup(Stage stage, Model model, MainController mainController) {
		super.setup(stage, model, mainController);
		getModel().subscribeSemesterChanges(this);
		editSessionController.setup(stage, model, mainController);

		fullReloadNecessary = true;
		updateCoursesTreeView();
	}

	@Override
	public void onNext(Semester item) {
		updateCoursesTreeView();
		getSubscription().request(1);
	}
}
