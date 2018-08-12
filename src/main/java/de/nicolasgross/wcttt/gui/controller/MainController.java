package de.nicolasgross.wcttt.gui.controller;

import de.nicolasgross.wcttt.gui.model.Model;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class MainController extends Controller {

	@FXML
	private MainMenuBarController menuBarController;
	@FXML
	private MainSideMenuController sideMenuController;
	@FXML
	private MainTableController timetableTableController;
	@FXML
	private Label stateInfo;


	@Override
	public void setup(Stage stage, Model model, MainController mainController) {
		super.setup(stage, model, mainController);
		setCloseConfirmation();
		menuBarController.setup(stage, model, this);
		sideMenuController.setup(stage, model, this);
		timetableTableController.setup(stage, model, this);
	}

	MainMenuBarController getMenuBarController() {
		return menuBarController;
	}

	MainSideMenuController getSideMenuController() {
		return sideMenuController;
	}

	MainTableController getTimetableTableController() {
		return timetableTableController;
	}

	private void setCloseConfirmation() {
		getStage().setOnCloseRequest(event -> {
			if (getModel().isChanged().getValue() &&
					!Util.confirmationAlert("Warning!", "There are unsaved " +
							"changes", "Closing the program will result in " +
							"the loss of all unsaved changes.")) {
				event.consume();
			} else {
				getStage().close();
			}
		});
	}

}
