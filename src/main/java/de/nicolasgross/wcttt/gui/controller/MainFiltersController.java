package de.nicolasgross.wcttt.gui.controller;

import de.nicolasgross.wcttt.gui.model.Model;
import de.nicolasgross.wcttt.lib.model.Chair;
import de.nicolasgross.wcttt.lib.model.Curriculum;
import de.nicolasgross.wcttt.lib.model.Semester;
import de.nicolasgross.wcttt.lib.model.Teacher;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.stage.Stage;

public class MainFiltersController extends SubscriptionController {

	@FXML
	private ComboBox<Teacher> teacherSelection;
	@FXML
	private ComboBox<Chair> chairSelection;
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
			curriculumSelection.getSelectionModel().clearSelection();
			// TODO
		});

		filterButton.setOnAction(event -> {
			// TODO
		});

	}

	@Override
	public void setup(Stage stage, Model model) {
		super.setup(stage, model);
		model.subscribe(this);
		updateGui();
	}

	@Override
	public void onNext(Semester item) {
		updateGui();
		getSubscription().request(1);
	}

	public void updateGui() {
		teacherSelection.setItems(getModel().getTeachers());
		chairSelection.setItems(getModel().getChairs());
		curriculumSelection.setItems(getModel().getCurricula());
	}

}
