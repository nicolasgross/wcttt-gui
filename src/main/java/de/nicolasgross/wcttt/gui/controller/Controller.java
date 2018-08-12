package de.nicolasgross.wcttt.gui.controller;

import de.nicolasgross.wcttt.gui.WctttGuiFatalException;
import de.nicolasgross.wcttt.gui.model.Model;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

public abstract class Controller {

	private Stage stage;
	private Model model;
	private MainController mainController;

	Stage getStage() {
		return stage;
	}

	Model getModel() {
		return model;
	}

	MainController getMainController() {
		return mainController;
	}

	public void setup(Stage stage, Model model, MainController mainController) {
		this.stage = stage;
		this.model = model;
		this.mainController = mainController;
	}

	void showFxmlWindow(String fxmlPath, String title, int minWidth,
	                    int minHeight) {
		FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
		Parent root;
		try {
			root = loader.load();
		} catch (IOException e) {
			throw new WctttGuiFatalException("Could not load '" + fxmlPath +
					"'", e);
		}
		Scene scene = new Scene(root);
		Stage stage = new Stage();
		stage.setScene(scene);

		Controller controller = loader.getController();
		controller.setup(stage, getModel(), getMainController());

		stage.titleProperty().bind(new SimpleStringProperty(title));
		stage.setMinWidth(minWidth);
		stage.setMinHeight(minHeight);
		stage.initModality(Modality.APPLICATION_MODAL);
		stage.show();
		root.requestFocus();
	}
}
