package de.nicolasgross.wcttt.gui.controller;

import de.nicolasgross.wcttt.gui.model.Model;
import javafx.stage.Stage;

public abstract class Controller  {

	private Stage stage;
	private Model model;


	Stage getStage() {
		return stage;
	}

	Model getModel() {
		return model;
	}

	public void setup(Stage stage, Model model) {
		this.stage = stage;
		this.model = model;
	}

}
