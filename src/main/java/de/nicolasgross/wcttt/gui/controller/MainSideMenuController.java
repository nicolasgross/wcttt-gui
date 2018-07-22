package de.nicolasgross.wcttt.gui.controller;

import de.nicolasgross.wcttt.gui.model.Model;
import javafx.fxml.FXML;
import javafx.scene.control.Separator;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class MainSideMenuController extends Controller {

	@FXML
	private MainTimetablesController timetablesController;
	@FXML
	private MainFiltersController filtersController;

	@FXML
	private VBox sideMenuVBox;
	@FXML
	private GridPane timetables;
	@FXML
	private Separator sideMenuVBoxSeparator;
	@FXML
	private VBox filters;
	@FXML
	private ToggleButton timetablesToggle;
	@FXML
	private ToggleButton filtersToggle;


	@Override
	public void setup(Stage stage, Model model) {
		super.setup(stage, model);
		timetablesController.setup(stage, model);
		filtersController.setup(stage, model);
	}

	private void adjustSideMenuSeparators() {
		if(!sideMenuVBox.getChildren().contains(sideMenuVBoxSeparator) &&
				sideMenuVBox.getChildren().contains(timetables) &&
				sideMenuVBox.getChildren().contains(filters)) {
			sideMenuVBox.getChildren().add(1, sideMenuVBoxSeparator);
		} else {
			sideMenuVBox.getChildren().remove(sideMenuVBoxSeparator);
		}
	}

	@FXML
	protected void initialize() {
		timetablesToggle.setOnAction(event -> {
			if (timetablesToggle.isSelected()) {
				sideMenuVBox.getChildren().add(0, timetables);
			} else {
				sideMenuVBox.getChildren().remove(timetables);
			}
			adjustSideMenuSeparators();
		});

		filtersToggle.setOnAction(event -> {
			if (filtersToggle.isSelected()) {
				sideMenuVBox.getChildren().add(filters);
			} else {
				sideMenuVBox.getChildren().remove(filters);
			}
			adjustSideMenuSeparators();
		});
	}

}
