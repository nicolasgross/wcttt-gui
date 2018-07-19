package de.nicolasgross.wcttt.gui.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;

public class MainMenuBarController {

	@FXML
	private MenuBar menuBar;

	@FXML
	private Menu menuFile;
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

}
