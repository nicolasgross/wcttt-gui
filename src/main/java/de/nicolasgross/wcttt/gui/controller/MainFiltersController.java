package de.nicolasgross.wcttt.gui.controller;

import de.nicolasgross.wcttt.gui.model.Model;
import de.nicolasgross.wcttt.lib.model.*;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.stage.Stage;

public class MainFiltersController extends SubscriberController {

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
			// TODO
			teacherSelection.getSelectionModel().clearSelection();
			chairSelection.getSelectionModel().clearSelection();
			courseSelection.getSelectionModel().clearSelection();
			curriculumSelection.getSelectionModel().clearSelection();
		});

		filterButton.setOnAction(event -> {
			// TODO
		});

	}

	@Override
	public void setup(Stage stage, Model model, MainController mainController) {
		super.setup(stage, model, mainController);
		model.subscribe(this);
		updateGui();
	}

	@Override
	public void onNext(Semester item) {
		Platform.runLater(this::updateGui);
		getSubscription().request(1);
	}

	private void updateGui() {
		teacherSelection.setItems(getModel().getTeachers());
		chairSelection.setItems(getModel().getChairs());
		courseSelection.setItems(getModel().getCourses());
		curriculumSelection.setItems(getModel().getCurricula());
	}

}
