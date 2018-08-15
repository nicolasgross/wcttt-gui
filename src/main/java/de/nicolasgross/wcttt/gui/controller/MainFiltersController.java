package de.nicolasgross.wcttt.gui.controller;

import de.nicolasgross.wcttt.gui.model.Model;
import de.nicolasgross.wcttt.lib.model.*;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.stage.Stage;

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
	public void setup(Stage stage, Model model, MainController mainController) {
		super.setup(stage, model, mainController);
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
			teacherSelection.setItems(getModel().getTeachers());
			chairSelection.setItems(getModel().getChairs());
			courseSelection.setItems(getModel().getCourses());
			curriculumSelection.setItems(getModel().getCurricula());
		});
	}
}
