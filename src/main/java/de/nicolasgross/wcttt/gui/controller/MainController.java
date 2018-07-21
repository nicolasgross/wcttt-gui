package de.nicolasgross.wcttt.gui.controller;

import de.nicolasgross.wcttt.gui.model.Model;
import javafx.fxml.FXML;
import javafx.scene.Scene;
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
	public void setup(Scene scene, Model model) {
		super.setup(scene, model);
		menuBarController.setup(scene, model);
		sideMenuController.setup(scene, model);
		tableController.setup(scene, model);
	}

}
