package de.nicolasgross.wcttt.gui.controller;

import de.nicolasgross.wcttt.gui.model.Model;
import de.nicolasgross.wcttt.lib.model.Course;
import de.nicolasgross.wcttt.lib.model.Semester;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class EditCoursesController extends SubscriberController<Semester> {

	@FXML
	private BorderPane rootPane;
	@FXML
	private VBox editCourseVBox;
	@FXML
	private TreeView<Course> coursesTreeView;
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


	@Override
	public void setup(Stage stage, Model model, MainController mainController) {
		super.setup(stage, model, mainController);
		getModel().subscribeSemesterChanges(this);
	}

	@Override
	public void onNext(Semester item) {
		// TODO
		getSubscription().request(1);
	}

}
