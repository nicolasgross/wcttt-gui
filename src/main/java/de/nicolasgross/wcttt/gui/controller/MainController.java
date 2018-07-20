package de.nicolasgross.wcttt.gui.controller;

import de.nicolasgross.wcttt.gui.model.Model;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class MainController extends Controller {

	@FXML
	private MainMenuBarController menuBarController;
	@FXML
	private MainSideMenuController sideMenuController;
	@FXML
	private MainTableController tableController;
	@FXML
	private Label stateInfo;


	@Override
	public void setModel(Model model) {
		super.setModel(model);
		menuBarController.setModel(model);
		sideMenuController.setModel(model);
		tableController.setModel(model);
	}

}
