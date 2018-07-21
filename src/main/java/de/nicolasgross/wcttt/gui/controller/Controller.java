package de.nicolasgross.wcttt.gui.controller;

import de.nicolasgross.wcttt.gui.model.Model;
import javafx.scene.Scene;

public abstract class Controller  {

	private Scene scene;
	private Model model;


	Scene getScene() {
		return scene;
	}

	void setScene(Scene scene) {
		this.scene = scene;
	}

	Model getModel() {
		return model;
	}

	void setModel(Model model) {
		this.model = model;
	}

	public void setup(Scene scene, Model model) {
		this.scene = scene;
		this.model = model;
	}

}
