/*
 * WCT³ (WIAI Course Timetabling Tool) is a software that strives to automate
 * the timetabling process at the WIAI faculty of the University of Bamberg.
 *
 * WCT³ GUI comprises functionality to view generated timetables, edit semester
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

package de.nicolasgross.wcttt.gui.controller;

import de.nicolasgross.wcttt.gui.model.Model;
import de.nicolasgross.wcttt.lib.binder.WctttBinder;
import de.nicolasgross.wcttt.lib.binder.WctttBinderException;
import de.nicolasgross.wcttt.lib.model.Semester;
import de.nicolasgross.wcttt.lib.model.SemesterImpl;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.MenuItem;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.File;
import java.util.Optional;

public class MainMenuBarController extends Controller {

	private static String EDIT_SEMESTER_FXML = "/fxml/edit-semester.fxml";
	private static String EDIT_COURSES_FXML = "/fxml/edit-courses.fxml";
	private static String EDIT_ROOMS_FXML = "/fxml/edit-rooms.fxml";
	private static String EDIT_CHAIRS_FXML = "/fxml/edit-chairs.fxml";
	private static String EDIT_CURRICULA_FXML = "/fxml/edit-curricula.fxml";
	private static String EDIT_GENERATE = "/fxml/edit-generate-algorithm.fxml";

	@FXML
	private MenuItem fileNew;
	@FXML
	private MenuItem fileOpen;
	@FXML
	private MenuItem fileSave;
	@FXML
	private MenuItem fileSaveAs;
	@FXML
	private MenuItem fileQuit;

	@FXML
	private MenuItem editSemester;
	@FXML
	private MenuItem editCourses;
	@FXML
	private MenuItem editRooms;
	@FXML
	private MenuItem editChairs;
	@FXML
	private MenuItem editCurricula;
	@FXML
	private MenuItem editGenerate;

	@FXML
	private MenuItem viewSessionSession;
	@FXML
	private MenuItem vewSessionRoom;
	@FXML
	private MenuItem viewTeacherPeriod;

	@FXML
	private MenuItem helpHelp;
	@FXML
	private MenuItem helpAbout;

	private WctttBinder binder;


	@FXML
	protected void initialize() {
		initFileMenu();
		initEditMenu();
		initViewMenu();
		initHelpMenu();
	}

	private void initFileMenu() {
		fileNew.setOnAction(event -> {
			if (!lossOfUnsavedConfirmed()) {
				return;
			}
			Semester semester = new SemesterImpl();
			getModel().setSemester(null, semester);
			binder = null;
		});

		fileOpen.setOnAction(event -> {
			if (!lossOfUnsavedConfirmed()) {
				return;
			}
			Optional<File> file = Util.chooseFileToOpenDialog(
					getStage().getScene().getWindow());
			if (file.isPresent()) {
				try {
					WctttBinder binder = new WctttBinder(file.get());
					Semester semester = binder.parse();
					this.binder = binder;
					getModel().setSemester(file.get().toPath(), semester);
				} catch (WctttBinderException e) {
					Util.exceptionAlert(e);
				}
			}
		});

		EventHandler<ActionEvent> fileSaveAsAction = event -> {
			Optional<File> file = Util.chooseFileToSaveDialog(
					getStage().getScene().getWindow());
			if (file.isPresent()) {
				try {
					WctttBinder newBinder = new WctttBinder(file.get());
					newBinder.write(getModel().getSemester());
					this.binder = newBinder;
					getModel().setXmlPath(file.get().toPath());
					getModel().setChanged(false);
				} catch (WctttBinderException e) {
					Util.exceptionAlert(e);
				}
			}
		};

		fileSave.setOnAction(event -> {
			if (getModel().getXmlPath().isPresent()) {
				assert binder != null;
				assert binder.getXmlFile().toPath().equals(
						getModel().getXmlPath().get());
				try {
					binder.write(getModel().getSemester());
					getModel().setChanged(false);
				} catch (WctttBinderException e) {
					Util.exceptionAlert(e);
				}
			} else {
				fileSaveAsAction.handle(event);
			}
		});

		fileSaveAs.setOnAction(fileSaveAsAction);

		fileQuit.setOnAction(event -> {
			getStage().getOnCloseRequest().handle(
					new WindowEvent(getStage().getScene().getWindow(),
							WindowEvent.WINDOW_CLOSE_REQUEST));
		});
	}

	private void initEditMenu() {
		editSemester.setOnAction(event ->
				showFxmlWindow(EDIT_SEMESTER_FXML, "Edit semester data", 550, 510));

		editCourses.setOnAction(event ->
				showFxmlWindow(EDIT_COURSES_FXML, "Edit course data", 600, 550));

		editRooms.setOnAction(event ->
				showFxmlWindow(EDIT_ROOMS_FXML, "Edit rooms", 600, 450));

		editChairs.setOnAction(event ->
				showFxmlWindow(EDIT_CHAIRS_FXML, "Edit chairs", 600, 450));

		editCurricula.setOnAction(event ->
				showFxmlWindow(EDIT_CURRICULA_FXML, "Edit curricula", 600, 450));

		editGenerate.setOnAction(event ->
				showFxmlWindow(EDIT_GENERATE, "Generate timetable", 400, 160));
	}

	private void initViewMenu() {
		// TODO
	}

	private void initHelpMenu() {
		// TODO
	}

	private void showFxmlWindow(String fxmlPath, String title, int minWidth,
	                    int minHeight) {
		Stage stage = Util.loadFxml(fxmlPath, null, null, getModel(),
				getMainController());
		Util.showStage(stage, title, minWidth, minHeight);
	}

	@Override
	public void setup(Stage stage, Model model, MainController mainController) {
		super.setup(stage, model, mainController);
		fileSave.disableProperty().bind(getModel().isChanged().not());
	}

	private boolean lossOfUnsavedConfirmed() {
		if (getModel().isChanged().getValue()) {
			return Util.confirmationAlert("There are unsaved changes",
					"Loading a new semester will result in the loss of all " +
							"unsaved changes. Do you want to proceed?");
		} else {
			return true;
		}
	}
}
