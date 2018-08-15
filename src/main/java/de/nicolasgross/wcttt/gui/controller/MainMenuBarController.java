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
	private MenuItem viewCourseCourse;
	@FXML
	private MenuItem vewCourseRoom;
	@FXML
	private MenuItem viewTeacherTimeslot;

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
				showFxmlWindow(EDIT_SEMESTER_FXML, "Edit semester data", 450, 535));

		editCourses.setOnAction(event ->
				showFxmlWindow(EDIT_COURSES_FXML, "Edit course data", 600, 550));

		editRooms.setOnAction(event ->
				showFxmlWindow(EDIT_ROOMS_FXML, "Edit rooms", 600, 450));

		// TODO
	}

	private void initViewMenu() {
		// TODO
	}

	private void initHelpMenu() {
		// TODO
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
