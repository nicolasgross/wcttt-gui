package de.nicolasgross.wcttt.gui.controller;

import de.nicolasgross.wcttt.gui.model.Model;
import javafx.stage.Stage;

public abstract class Controller {

	private Stage stage;
	private Model model;
	private MainController mainController;

	public Controller() { }

	public Controller(Stage stage, Model model, MainController mainController) {
		setup(stage, model, mainController);
	}

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

}
