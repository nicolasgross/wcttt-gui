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
import javafx.stage.Stage;

/**
 * An abstract controller class that bundles functionality that is commonly used
 * in a controller.
 */
public abstract class Controller {

	private Stage stage;
	private Model model;
	private MainController mainController;
	private HostServices hostServices;

	Stage getStage() {
		return stage;
	}

	HostServices getHostServices() {
		return hostServices;
	}

	MainController getMainController() {
		return mainController;
	}

	Model getModel() {
		return model;
	}

	public void setup(Stage stage, HostServices hostServices,
	                  MainController mainController, Model model) {
		this.stage = stage;
		this.hostServices = hostServices;
		this.mainController = mainController;
		this.model = model;
	}
}
