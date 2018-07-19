package de.nicolasgross.wcttt.gui.controller;

import de.nicolasgross.wcttt.gui.model.Model;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class MainController {

	@FXML
	private MainMenuBarController menuBarController;
	@FXML
	private MainSideMenuController sideMenuController;
	@FXML
	private MainTimetablesController timetablesController;
	@FXML
	private MainFiltersController filtersController;
	@FXML
	private MainTableController tableController;
	@FXML
	private Label stateInfo;

	private Model model;


	public void setModel(Model model) {
		this.model = model;
	}

}
