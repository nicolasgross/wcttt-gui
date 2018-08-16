package de.nicolasgross.wcttt.gui.controller;

import de.nicolasgross.wcttt.gui.model.Model;
import de.nicolasgross.wcttt.lib.model.Period;
import de.nicolasgross.wcttt.lib.model.Teacher;
import de.nicolasgross.wcttt.lib.model.WctttModelException;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.Collections;
import java.util.LinkedList;

public class EditChairTeachersController extends Controller {

	@FXML
	private VBox editTeacherVBox;
	@FXML
	private TextField nameField;
	@FXML
	private ListView<Period> unfavorableListView;
	@FXML
	private Button addUnfavorableButton;
	@FXML
	private Button removeUnfavorableButton;
	@FXML
	private ListView<Period> unavailableListView;
	@FXML
	private Button addUnavailableButton;
	@FXML
	private Button removeUnavailableButton;
	@FXML
	private ListView<Period> periodsListView;
	@FXML
	private Button applyButton;

	private Teacher selectedTeacher;

	@FXML
	protected void initialize() {
		addUnfavorableButton.setOnAction(event -> addPeriodAction(true));
		removeUnfavorableButton.setOnAction(event -> removePeriodAction(true));

		addUnavailableButton.setOnAction(event -> addPeriodAction(false));
		removeUnavailableButton.setOnAction(event -> removePeriodAction(false));

		applyButton.setOnAction(event -> applyButtonAction());
	}

	private void addPeriodAction(boolean isUnfavorable) {
		Period selected = periodsListView.getSelectionModel().getSelectedItem();
		if (selected == null) {
			return;
		}
		periodsListView.getItems().remove(selected);
		if (isUnfavorable) {
			unfavorableListView.getItems().add(selected);
			Collections.sort(unfavorableListView.getItems());
		} else {
			unavailableListView.getItems().add(selected);
			Collections.sort(unavailableListView.getItems());
		}
	}

	private void removePeriodAction(boolean isUnfavorable) {
		Period selected;
		if (isUnfavorable) {
			selected = unfavorableListView.getSelectionModel().getSelectedItem();
		} else {
			selected = unavailableListView.getSelectionModel().getSelectedItem();
		}
		if (selected == null) {
			return;
		}
		if (isUnfavorable) {
			unfavorableListView.getItems().remove(selected);
			periodsListView.getItems().add(selected);
		} else {
			unavailableListView.getItems().remove(selected);
			periodsListView.getItems().add(selected);
		}
		Collections.sort(periodsListView.getItems());
	}

	private void applyButtonAction() {
		assert selectedTeacher != null;
		try {
			getModel().updateTeacherData(selectedTeacher, nameField.getText(),
					new LinkedList<>(unfavorableListView.getItems()),
					new LinkedList<>(unavailableListView.getItems()));
		} catch (WctttModelException e) {
			Util.errorAlert("Problem with editing the teacher",
					e.getMessage());
		}
	}

	@Override
	public void setup(Stage stage, Model model, MainController mainController) {
		super.setup(stage, model, mainController);
	}

	VBox getEditTeacherVBox(Teacher selected) {
		this.selectedTeacher = selected;
		Platform.runLater(() -> updateSessionEditVBox(selected));
		return editTeacherVBox;
	}

	private void updateSessionEditVBox(Teacher selected) {
		if (selected == null) {
			nameField.setText("");
			unfavorableListView.getItems().clear();
			unavailableListView.getItems().clear();
			periodsListView.getItems().clear();
			periodsListView.getItems().addAll(getModel().getPeriods());
		} else {
			nameField.setText(selected.getName());
			unfavorableListView.getItems().clear();
			unavailableListView.getItems().clear();
			periodsListView.getItems().clear();
			for (Period period : getModel().getPeriods()) {
				if (selected.getUnfavorablePeriods().contains(period)) {
					unfavorableListView.getItems().add(period);
				} else if (selected.getUnavailablePeriods().contains(period)) {
					unavailableListView.getItems().add(period);
				} else {
					periodsListView.getItems().add(period);
				}
			}
		}
	}
}
