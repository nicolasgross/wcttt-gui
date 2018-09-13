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
import wcttt.lib.model.Period;
import wcttt.lib.model.Teacher;
import wcttt.lib.model.WctttModelException;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.Collections;
import java.util.LinkedList;

/**
 * Controller for the view that is concerned with editing the teachers of a
 * chair.
 */
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
	public void setup(Stage stage, HostServices hostServices, MainController mainController, Model model) {
		super.setup(stage, hostServices, mainController, model);
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
