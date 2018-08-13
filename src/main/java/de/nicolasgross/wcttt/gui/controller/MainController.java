package de.nicolasgross.wcttt.gui.controller;

import de.nicolasgross.wcttt.gui.model.Model;
import javafx.application.Platform;
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
		getModel().getStateTextProperty().addListener(
				(observable, oldValue, newValue) -> updateStateInfo(newValue));
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

	private void updateStateInfo(String newValue) {
		Platform.runLater(() -> stateInfo.setText(newValue));
	}

	private void setCloseConfirmation() {
		getStage().setOnCloseRequest(event -> {
			if (getModel().isChanged().getValue() &&
					!Util.confirmationAlert("There are unsaved changes",
							"Closing the program will result in the loss of " +
									"all unsaved changes. Do you want to " +
									"proceed?")) {
				event.consume();
			} else {
				getStage().close();
				getModel().close();
			}
		});
	}

}
