package de.nicolasgross.wcttt.gui.controller;

import de.nicolasgross.wcttt.core.algorithms.Algorithm;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;

public class EditGenerateParameterController extends Controller {

	@FXML
	private GridPane parameterGridPane;
	@FXML
	private Button startButton;

	private Algorithm selectedAlgorithm;

	@FXML
	protected void initialize() {
		startButton.setOnAction(event -> startButtonAction());
	}

	private void startButtonAction() {
		// TODO
	}

	void setSelectedAlgorithm(Algorithm selectedAlgorithm) {
		this.selectedAlgorithm = selectedAlgorithm;
		adjustInputFields();
	}

	private void adjustInputFields() {
		// TODO
	}

}
