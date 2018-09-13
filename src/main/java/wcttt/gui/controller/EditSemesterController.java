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
import wcttt.gui.WctttGuiException;
import wcttt.gui.model.Model;
import wcttt.lib.model.ConstraintWeightings;
import wcttt.lib.model.ValidationHelper;
import wcttt.lib.model.WctttModelException;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Controller for the view that is concerned with editing the properties of a
 * semester.
 */
public class EditSemesterController extends Controller {

	private static final List<Integer> DAYS_PER_WEEK_ITEMS = IntStream.range(
			ValidationHelper.DAYS_PER_WEEK_MIN,
			ValidationHelper.DAYS_PER_WEEK_MAX + 1).boxed().
			collect(Collectors.toList());
	private static final List<Integer> TIME_SLOTS_ITEMS = IntStream.range(
			ValidationHelper.TIME_SLOTS_PER_DAY_MIN,
			ValidationHelper.TIME_SLOTS_PER_DAY_MAX + 1).boxed().
			collect(Collectors.toList());

	@FXML
	private TextField nameField;
	@FXML
	private ChoiceBox<Integer> daysPerWeekBox;
	@FXML
	private ChoiceBox<Integer> timeSlotsBox;
	@FXML
	private TextField maxLecturesField;
	@FXML
	private TextField s1Field;
	@FXML
	private TextField s2Field;
	@FXML
	private TextField s3Field;
	@FXML
	private TextField s4Field;
	@FXML
	private TextField s5Field;
	@FXML
	private TextField s6Field;
	@FXML
	private TextField s7Field;
	@FXML
	private Button applyButton;

	@FXML
	protected void initialize() {
		applyButton.setOnAction(event -> {
			List<Exception> excList = new ArrayList<>(4);

			setName();
			try {
				setDaysPerWeek();
			} catch (WctttModelException e) {
				excList.add(e);
			}
			try {
				setTimeSlots();
			} catch (WctttModelException e) {
				excList.add(e);
			}
			try {
				setMaxLectures();
			} catch (WctttGuiException | WctttModelException e) {
				excList.add(e);
			}
			try {
				setSoftConstraintWeightings();
			} catch (WctttGuiException | WctttModelException e) {
				excList.add(e);
			}

			if (!excList.isEmpty()) {
				StringBuilder errors = new StringBuilder();
				for (int i = 0; i < excList.size(); i++) {
					if (i > 0) {
						errors.append(System.getProperty("line.separator"));
						errors.append(System.getProperty("line.separator"));
					}
					errors.append(i + 1).append(". ");
					errors.append(excList.get(i).getMessage());
				}
				Util.errorAlert("Odd parameter values detected",
						errors.toString());
			}
		});
	}

	@Override
	public void setup(Stage stage, HostServices hostServices,
	                  MainController mainController, Model model) {
		super.setup(stage, hostServices, mainController, model);

		nameField.setText(getModel().getName());

		daysPerWeekBox.setItems(FXCollections.observableList(
				DAYS_PER_WEEK_ITEMS));
		daysPerWeekBox.setValue(getModel().getDaysPerWeek());

		timeSlotsBox.setItems(FXCollections.observableList(TIME_SLOTS_ITEMS));
		timeSlotsBox.setValue(getModel().getTimeSlotsPerDay());

		maxLecturesField.setText(String.valueOf(getModel().
				getMaxDailyLecturesPerCur()));

		s1Field.setText(String.valueOf(getModel().getConstrWeightings().
				getS1()));
		s2Field.setText(String.valueOf(getModel().getConstrWeightings().
				getS2()));
		s3Field.setText(String.valueOf(getModel().getConstrWeightings().
				getS3()));
		s4Field.setText(String.valueOf(getModel().getConstrWeightings().
				getS4()));
		s5Field.setText(String.valueOf(getModel().getConstrWeightings().
				getS5()));
		s6Field.setText(String.valueOf(getModel().getConstrWeightings().
				getS6()));
		s7Field.setText(String.valueOf(getModel().getConstrWeightings().
				getS7()));
	}

	private void setName() {
		if (!getModel().getName().equals(nameField.getText())) {
			getModel().setName(nameField.getText());
		}
	}

	private void setDaysPerWeek() throws WctttModelException {
		if (getModel().getDaysPerWeek() != daysPerWeekBox.getValue()) {
			getModel().setDaysPerWeek(daysPerWeekBox.getValue());
		}
	}

	private void setTimeSlots() throws WctttModelException {
		if (getModel().getTimeSlotsPerDay() != timeSlotsBox.getValue()) {
			getModel().setTimeSlotsPerDay(timeSlotsBox.getValue());
		}
	}

	private void setMaxLectures() throws WctttGuiException,
			WctttModelException {
		int newMaxLectures;
		try {
			newMaxLectures = Integer.parseInt(maxLecturesField.getText());
		} catch (NumberFormatException e) {
			throw new WctttGuiException("The maximum number of daily " +
					"lectures per curriculum must be an integer >= " +
					ValidationHelper.MIN_DAILY_LECTURES_PER_CUR_MIN);
		}
		if (getModel().getMaxDailyLecturesPerCur() != newMaxLectures) {
			getModel().setMaxDailyLecturesPerCur(newMaxLectures);
		}
	}

	private void setSoftConstraintWeightings() throws WctttGuiException,
			WctttModelException {
		TextField weightingFields[] = {s1Field, s2Field, s3Field, s4Field,
				s5Field, s6Field, s7Field};
		double weightingValues[] = new double[8];
		for (int i = 0; i < 7; i++) {
			try {
				weightingValues[i] = Double.parseDouble(
						weightingFields[i].getText());
			} catch (NumberFormatException e) {
				throw new WctttGuiException("The constraint weightings must " +
						"be a floating point number >= " +
						ValidationHelper.CONSTRAINT_WEIGHTING_MIN);
			}
		}
		ConstraintWeightings weightings = new ConstraintWeightings(
				weightingValues[0], weightingValues[1], weightingValues[2],
				weightingValues[3], weightingValues[4], weightingValues[5],
				weightingValues[6]);
		if (!getModel().getConstrWeightings().equals(weightings)) {
			getModel().setConstrWeightings(weightings);
		}
	}
}
