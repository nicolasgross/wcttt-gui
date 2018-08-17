package de.nicolasgross.wcttt.gui.controller;

import de.nicolasgross.wcttt.core.algorithms.Algorithm;
import de.nicolasgross.wcttt.core.algorithms.tabu_based_memetic_approach.TabuBasedMemeticApproach;
import de.nicolasgross.wcttt.gui.WctttGuiFatalException;
import de.nicolasgross.wcttt.gui.model.Model;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public class EditGenerateAlgorithmController extends Controller {

	private static String EDIT_GENERATE_PARAMETERS =
			"/fxml/edit-generate-parameters.fxml";

	@FXML
	private ChoiceBox<Algorithm> algorithmChoiceBox;
	@FXML
	private Button okButton;

	private Algorithm selectedAlgorithm;

	@FXML
	protected void initialize() {
		okButton.setOnAction(event -> okButtonAction());
	}

	private void okButtonAction() {
		selectedAlgorithm = algorithmChoiceBox.getValue();
		showParametersWindow();
	}

	void showParametersWindow() {
		FXMLLoader loader = new FXMLLoader(getClass().getResource(
				EDIT_GENERATE_PARAMETERS));
		Parent root;
		try {
			root = loader.load();
		} catch (IOException e) {
			throw new WctttGuiFatalException("Could not load '" +
					EDIT_GENERATE_PARAMETERS + "'", e);
		}

		Scene scene = new Scene(root);

		EditGenerateParameterController controller = loader.getController();
		controller.setup(getStage(), getModel(), getMainController());
		controller.setSelectedAlgorithm(selectedAlgorithm);

		getStage().titleProperty().bind(
				new SimpleStringProperty("Enter algorithm parameters"));
		getStage().setMinWidth(400);
		getStage().setMinHeight(250);
		getStage().setScene(scene);
		root.requestFocus();
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
}
