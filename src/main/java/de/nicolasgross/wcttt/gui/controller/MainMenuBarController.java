package de.nicolasgross.wcttt.gui.controller;

import de.nicolasgross.wcttt.lib.binder.WctttBinder;
import de.nicolasgross.wcttt.lib.binder.WctttBinderException;
import de.nicolasgross.wcttt.lib.model.Semester;
import de.nicolasgross.wcttt.lib.model.SemesterImpl;
import javafx.fxml.FXML;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;

import java.io.File;
import java.util.Optional;

public class MainMenuBarController extends Controller {

	@FXML
	private MenuBar menuBar;

	@FXML
	private Menu menuFile;
	@FXML
	private MenuItem menuFileNew;
	@FXML
	private MenuItem menuFileOpen;
	@FXML
	private MenuItem menuFileSave;
	@FXML
	private MenuItem menuFileSaveAs;
	@FXML
	private MenuItem menuFileCloseDb;
	@FXML
	private MenuItem menuFileQuit;

	@FXML
	private Menu menuEdit;
	@FXML
	private MenuItem menuEditSemester;
	@FXML
	private MenuItem menuEditCourses;
	@FXML
	private MenuItem menuEditRooms;
	@FXML
	private MenuItem menuEditChairs;
	@FXML
	private MenuItem menuEditCurricula;
	@FXML
	private MenuItem menuEditGenerate;

	@FXML
	private Menu menuView;
	@FXML
	private MenuItem menuViewCourseCourse;
	@FXML
	private MenuItem menuViewCourseRoom;
	@FXML
	private MenuItem menuViewTeacherTimeslot;

	@FXML
	private Menu menuHelp;
	@FXML
	private MenuItem menuHelpHelp;
	@FXML
	private MenuItem menuHelpAbout;

	private WctttBinder binder;


	@FXML
	protected void initialize() {
		menuFileNew.setOnAction(event -> {
			if (lossOfUnsavedUnconfirmed()) {
				return;
			}
			Semester semester = new SemesterImpl();
			getModel().setSemester(null, semester);
		});

		menuFileOpen.setOnAction(event -> {
			if (lossOfUnsavedUnconfirmed()) {
				return;
			}
			Optional<File> file = Util.choosePathAlert(getScene().getWindow());
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
	}

	private boolean lossOfUnsavedUnconfirmed() {
		if (!getModel().isUnchanged()) {
			return (!Util.confirmationAlert("Warning!", "There are unsaved " +
					"changes", "Loading a new semester will result in the" +
					" loss of all unsaved changes."));
		} else {
			return false;
		}
	}

}
