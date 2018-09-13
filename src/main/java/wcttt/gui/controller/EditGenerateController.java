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
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import wcttt.gui.WctttGuiException;
import wcttt.gui.WctttGuiFatalException;
import wcttt.gui.model.Model;
import wcttt.lib.algorithms.*;
import wcttt.lib.algorithms.tabu_based_memetic_approach.TabuBasedMemeticApproach;
import wcttt.lib.model.Timetable;
import wcttt.lib.model.WctttModelException;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Controller for the views and alerts that are concerned with generating new
 * timetables.
 */
public class EditGenerateController extends Controller {

	private static String EDIT_GENERATE_PARAMETERS =
			"/fxml/edit-generate-parameters.fxml";
	private static String EDIT_GENERATE_RUNNING =
			"/fxml/edit-generate-running.fxml";

	// Algorithm selection window
	@FXML
	private ChoiceBox<Algorithm> algorithmChoiceBox;
	@FXML
	private Button okButton;

	private Algorithm selectedAlgorithm;

	// Parameters window
	@FXML
	private GridPane parameterGridPane;
	@FXML
	private Button startButton;

	private List<TextField> parameterInputs = new ArrayList<>();

	// Running window
	@FXML
	private ProgressBar progressBar;
	@FXML
	private Button cancelButton;

	private AtomicBoolean foundFeasibleSolution = new AtomicBoolean(false);
	private Thread algorithmThread;


	@FXML
	protected void initialize() {
		okButton.disableProperty().bind(
				algorithmChoiceBox.valueProperty().isNull());
		okButton.setOnAction(event -> {
				selectedAlgorithm = algorithmChoiceBox.getValue();
				showParametersWindow();
		});
	}

	private void initializeParametersWindow() {
		startButton.setOnAction(event -> {
			if (setParameters()) {
				runAlgorithm();
			}
		});
	}

	private void initializeRunningWindow() {
		progressBar.setProgress(ProgressIndicator.INDETERMINATE_PROGRESS);
		progressBar.prefWidthProperty().bind(getStage().widthProperty().
				subtract(25));

		cancelButton.setOnAction(event -> {
			cancelButton.disableProperty().setValue(true);
			selectedAlgorithm.cancel();
		});
	}

	@Override
	public void setup(Stage stage, HostServices hostServices,
	                  MainController mainController, Model model) {
		super.setup(stage, hostServices, mainController, model);

		// |-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-|
		// |  ADD NEW ALGORITHMS TO THIS LIST  |
		// |-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-|
		List<Algorithm> algorithms = new LinkedList<>();
		algorithms.add(new TabuBasedMemeticApproach(getModel()));

		algorithmChoiceBox.getItems().setAll(algorithms);
		algorithmChoiceBox.getSelectionModel().select(0);
	}

	private void showParametersWindow() {
		if (selectedAlgorithm.getParameters().isEmpty()) {
			try {
				selectedAlgorithm.setParameterValues(new LinkedList<>());
				runAlgorithm();
				return;
			} catch (WctttAlgorithmException e) {
				throw new WctttGuiFatalException("Implementation error in " +
						"algorithm '" + selectedAlgorithm + "', no parameter " +
						"specified but empty value list was rejected", e);
			}
		}
		Stage stage = Util.loadFxml(EDIT_GENERATE_PARAMETERS, this, getStage(),
				getModel(), getMainController());
		initializeParametersWindow();
		adjustParameterInputFields();
		Util.showStage(stage, "Enter algorithm parameters", 420, 300);
	}

	private void adjustParameterInputFields() {
		List<ParameterDefinition> parameters = selectedAlgorithm.getParameters();
		for (ParameterDefinition parameter : parameters) {
			TextField parameterField = new TextField();
			parameterField.setText(parameter.getDefaultValue());
			parameterField.setMaxWidth(100);
			parameterInputs.add(parameterField);
			parameterGridPane.addRow(parameterGridPane.getRowCount(),
					new Label(parameter.getName()), parameterField);
		}
	}

	private boolean setParameters() {
		List<ParameterDefinition> parameters = selectedAlgorithm.getParameters();
		List<ParameterValue> values = new ArrayList<>(parameterInputs.size());
		boolean errorOccured = false;
		for (int i = 0; i < parameterInputs.size(); i++) {
			if (parameters.get(i).getType() == ParameterType.INT) {
				try {
					ParameterValue<Integer> value = new ParameterValue<>(
							parameters.get(i),
							Integer.parseInt(parameterInputs.get(i).getText()));
					values.add(value);
				} catch (NumberFormatException e) {
					Util.errorAlert("Problem with parameter values",
							"Parameter '" + parameters.get(i).getName() +
									"' must be an integer");
					errorOccured = true;
				}
			} else {
				try {
					ParameterValue<Double> value = new ParameterValue<>(
							parameters.get(i),
							Double.parseDouble(parameterInputs.get(i).getText()));
					values.add(value);
				} catch (NumberFormatException e) {
					Util.errorAlert("Problem with parameter values",
							"Parameter '" + parameters.get(i).getName() +
									"' must be a double");
					errorOccured = true;
				}
			}
		}
		if (!errorOccured) {
			try {
				selectedAlgorithm.setParameterValues(values);
			} catch (WctttAlgorithmException e) {
				Util.errorAlert("Problem with parameter values", e.getMessage());
				errorOccured = true;
			}
		}
		return !errorOccured;
	}

	private void runAlgorithm() {
		Runnable algorithmRunnable = () -> {
			try {
				Timetable timetable = selectedAlgorithm.generate();
				if (timetable != null) {
					foundFeasibleSolution.set(true);
					getModel().addTimetable(timetable);
				}
			} catch (WctttModelException e) {
				Platform.runLater(() -> Util.exceptionAlert(
						new WctttGuiException("Generated timetable was " +
								"invalid, there is a bug in the algorithm", e)));
			} catch (WctttAlgorithmException e) {
				Platform.runLater(() -> Util.exceptionAlert(
						new WctttGuiException("A problem occurred while " +
								"running the algorithm", e)));
			}
		};
		algorithmThread = new Thread(algorithmRunnable);
		algorithmThread.start();
		Runnable finalizeRunnable = this::finalizeAlgorithmThread;
		Thread finalizeThread = new Thread(finalizeRunnable);
		finalizeThread.start();
		showRunningWindow();
	}

	private void showRunningWindow() {
		Stage stage = Util.loadFxml(EDIT_GENERATE_RUNNING, this, getStage(),
				getModel(), getMainController());
		initializeRunningWindow();
		Util.showStage(stage, "Algorithm running", 420, 160);
	}

	private void finalizeAlgorithmThread() {
		while (true) {
			try {
				algorithmThread.join();
				break;
			} catch (InterruptedException e) {
				// ignore
			}
		}
		Platform.runLater(() -> getStage().close());
		String message;
		if (foundFeasibleSolution.get()) {
			message = "A feasible timetable was found";
		} else {
			message = "No feasible timetable was found";
		}
		Platform.runLater(() ->
				Util.informationAlert("Outcome information", message));
	}
}
