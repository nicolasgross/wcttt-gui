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
	public void setup(Stage stage, Model model) {
		super.setup(stage, model);
		setCloseConfirmation();
		menuBarController.setup(stage, model);
		sideMenuController.setup(stage, model);
		timetableTableController.setup(stage, model);
	}

	private void setCloseConfirmation() {
		getStage().setOnCloseRequest(event -> {
			if (getModel().isChanged().getValue() &&
					!Util.confirmationAlert("Warning!", "There are unsaved " +
							"changes", "Closing the program will result in " +
							"the loss of all unsaved changes.")) {
				event.consume();
				return;
			} else {
				getStage().close();
			}
		});
	}

}
