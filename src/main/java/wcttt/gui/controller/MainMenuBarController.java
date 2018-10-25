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
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import wcttt.gui.model.Model;
import wcttt.lib.binder.WctttBinder;
import wcttt.lib.binder.WctttBinderException;
import wcttt.lib.model.Semester;
import wcttt.lib.model.SemesterImpl;

import java.io.File;
import java.util.Optional;

/**
 * Controller for the menu bar.
 */
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
	private MenuItem viewSessionRoom;
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
		// TODO implement
		viewSessionSession.setOnAction(event ->
				Util.informationAlert("Unimplemented feature",
						"This feature is not yet implemented."));

		// TODO implement
		viewSessionRoom.setOnAction(event ->
				Util.informationAlert("Unimplemented feature",
						"This feature is not yet implemented."));

		// TODO implement
		viewTeacherPeriod.setOnAction(event ->
				Util.informationAlert("Unimplemented feature",
						"This feature is not yet implemented."));
	}

	private void initHelpMenu() {
		helpHelp.setOnAction(event -> showHelpDialog());

		helpAbout.setOnAction(event -> showAboutDialog());
	}

	private void showHelpDialog() {
		Alert alert = new Alert(Alert.AlertType.INFORMATION);
		alert.setTitle("Help");
		alert.setHeaderText("Help");
		VBox mainVBox = new VBox();
		mainVBox.setSpacing(15);

		VBox thesisVBox = new VBox();
		Text text = new Text("Detailed information about the " +
			"usage, architecture and implementation of WCT³ can be found in " +
			"Nicolas Gross' bachelor thesis.");
		text.setWrappingWidth(400);
		thesisVBox.getChildren().add(text);
		Hyperlink thesisLink = new Hyperlink("Download thesis");
		thesisLink.setOnAction(event -> getHostServices().showDocument(
			"https://nicolasgross.de/files/bachelor-thesis.pdf"));
		thesisVBox.getChildren().add(thesisLink);
		mainVBox.getChildren().add(thesisVBox);

		text = new Text("Working features:" +
			System.lineSeparator() + "  - Semester data editing" +
			System.lineSeparator() + "  - Saving/loading semester data " +
			"to/from XML files" +
			System.lineSeparator() + "  - Generating new timetables" +
			System.lineSeparator() + "  - Timetable view including the " +
			"respective filters");
		text.setWrappingWidth(400);
		mainVBox.getChildren().add(text);

		text = new Text("Unimplemented features:" +
			System.lineSeparator() + "  - Session-session conflicts view" +
			System.lineSeparator() + "  - Session-room conflicts view" +
			System.lineSeparator() + "  - Teacher-period conflicts view");
		text.setWrappingWidth(400);
		mainVBox.getChildren().add(text);

		alert.getDialogPane().setContent(mainVBox);
		alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
		alert.getDialogPane().setPrefWidth(400);
		alert.showAndWait();
	}
	private void showAboutDialog() {
		Alert alert = new Alert(Alert.AlertType.INFORMATION);
		alert.setTitle("About WCT³");
		alert.setHeaderText("WIAI Course Timetabling Tool");
		alert.setContentText("WCT³ (WIAI Course Timetabling Tool) is a " +
			"software that strives to automate the timetabling process at the" +
			" WIAI faculty of the University of Bamberg. It was developed by " +
			"Nicolas Gross as part of his bachelor thesis at the Software " +
			"Technologies Research Group (SWT)." +
			System.lineSeparator() + System.lineSeparator() +
			"Version: " + getClass().getPackage().getImplementationVersion() +
			System.lineSeparator() + System.lineSeparator() +
			"Copyright © 2018 Nicolas Gross" + System.lineSeparator() +
			"This program comes with absolutely no warranty." +
			System.lineSeparator() +
			"See the GNU General Public License version 3 for details.");
		alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
		alert.getDialogPane().setPrefWidth(400);
		alert.showAndWait();
	}

	private void showFxmlWindow(String fxmlPath, String title, int minWidth,
	                    int minHeight) {
		Stage stage = Util.loadFxml(fxmlPath, null, null, getModel(),
				getMainController());
		Util.showStage(stage, title, minWidth, minHeight);
	}

	@Override
	public void setup(Stage stage, HostServices hostServices,
	                  MainController mainController, Model model) {
		super.setup(stage, hostServices, mainController, model);
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
