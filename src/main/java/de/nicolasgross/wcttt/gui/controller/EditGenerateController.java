package de.nicolasgross.wcttt.gui.controller;

import de.nicolasgross.wcttt.core.WctttCoreException;
import de.nicolasgross.wcttt.core.algorithms.Algorithm;
import de.nicolasgross.wcttt.core.algorithms.ParameterDefinition;
import de.nicolasgross.wcttt.core.algorithms.ParameterType;
import de.nicolasgross.wcttt.core.algorithms.ParameterValue;
import de.nicolasgross.wcttt.core.algorithms.tabu_based_memetic_approach.TabuBasedMemeticApproach;
import de.nicolasgross.wcttt.gui.WctttGuiException;
import de.nicolasgross.wcttt.gui.WctttGuiFatalException;
import de.nicolasgross.wcttt.gui.model.Model;
import de.nicolasgross.wcttt.lib.model.Timetable;
import de.nicolasgross.wcttt.lib.model.WctttModelException;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

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
			selectedAlgorithm.cancelTimetableCreation();
		});
	}

	@Override
	public void setup(Stage stage, Model model, MainController mainController) {
		super.setup(stage, model, mainController);

		// ADD NEW ALGORITHMS TO THIS LIST
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
			} catch (WctttCoreException e) {
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
			} catch (WctttCoreException e) {
				Util.errorAlert("Problem with parameter values", e.getMessage());
				errorOccured = true;
			}
		}
		return !errorOccured;
	}

	private void runAlgorithm() {
		Runnable algorithmRunnable = () -> {
			Timetable timetable = selectedAlgorithm.createTimetable();
			if (timetable != null) {
				try {
					foundFeasibleSolution.set(true);
					getModel().addTimetable(timetable);
				} catch (WctttModelException e) {
					Platform.runLater(() -> Util.exceptionAlert(
							new WctttGuiException("Generated timetable was " +
									"invalid, there is a bug in the algorithm",
									e)));
				}
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
