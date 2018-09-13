/*
 * WCT³ (WIAI Course Timetabling Tool) is a software that strives to automate
 * the timetabling process at the WIAI faculty of the University of Bamberg.
 *
 * WCT³-GUI comprises functionality to view generated timetables, edit semester
 * data and to generate new timetables.
 *
 * Copyright (C) 2018 Nicolas Gross
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package wcttt.gui.controller;

import javafx.application.HostServices;
import wcttt.gui.model.Model;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.stage.Stage;

/**
 * First controller that is created and responsible for the state info and
 * closing the window. Initializes the controllers for the menu bar, the side
 * menu and the timetable view.
 */
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
	public void setup(Stage stage, HostServices hostServices,
	                  MainController mainController, Model model) {
		super.setup(stage, hostServices, mainController, model);
		setCloseConfirmation();
		menuBarController.setup(stage, hostServices, this, model);
		sideMenuController.setup(stage, hostServices, this, model);
		timetableTableController.setup(stage, hostServices, this, model);
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
