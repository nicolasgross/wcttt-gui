package de.nicolasgross.wcttt.gui.controller;

import de.nicolasgross.wcttt.gui.model.Model;
import de.nicolasgross.wcttt.lib.binder.WctttBinder;
import de.nicolasgross.wcttt.lib.binder.WctttBinderException;
import de.nicolasgross.wcttt.lib.model.Semester;
import de.nicolasgross.wcttt.lib.model.SemesterImpl;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.MenuItem;

import java.io.File;
import java.util.Optional;

public class MainMenuBarController extends Controller {

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
					getScene().getWindow());
			if (file.isPresent()) {
				try {
					binder = new WctttBinder(file.get());
					Semester semester = binder.parse();
					getModel().setSemester(file.get().toPath(), semester);
				} catch (WctttBinderException e) {
					Util.exceptionAlert(e);
				}
			}
		});

		EventHandler<ActionEvent> fileSaveAsAction = event -> {
			Optional<File> file = Util.chooseFileToSaveDialog(
					getScene().getWindow());
			if (file.isPresent()) {
				try {
					binder = new WctttBinder(file.get());
					binder.write(getModel().getSemester());
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
					getModel().setChanged(true);
				} catch (WctttBinderException e) {
					Util.exceptionAlert(e);
				}
			} else {
				fileSaveAsAction.handle(event);
			}
		});

		fileSaveAs.setOnAction(fileSaveAsAction);

		fileQuit.setOnAction(event -> {

		});
	}

	@Override
	public void setup(Scene scene, Model model) {
		super.setup(scene, model);
		fileSave.disableProperty().bind(getModel().isChanged().not());
	}

	private boolean lossOfUnsavedConfirmed() {
		if (getModel().isChanged().getValue()) {
			return Util.confirmationAlert("Warning!", "There are unsaved " +
					"changes", "Loading a new semester will result in the" +
					" loss of all unsaved changes.");
		} else {
			return true;
		}
	}

}
