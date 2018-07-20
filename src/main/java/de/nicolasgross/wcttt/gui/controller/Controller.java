package de.nicolasgross.wcttt.gui.controller;

import de.nicolasgross.wcttt.gui.model.Model;

public abstract class Controller {

	private Model model;


	public Model getModel() {
		return model;
	}

	public void setModel(Model model) {
		this.model = model;
	}

}
