package de.nicolasgross.wcttt.gui.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Separator;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class MainSideMenuController {

	@FXML
	private HBox sideMenuHBox;
	@FXML
	private VBox sideMenuVBox;
	@FXML
	private GridPane timetablesPane;
	@FXML
	private Separator sideMenuVBoxSeparator;
	@FXML
	private GridPane filtersPane;
	@FXML
	private ToggleButton timetablesToggle;
	@FXML
	private ToggleButton filtersToggle;


	private void adjustSideMenuSeparators() {
		if(!sideMenuVBox.getChildren().contains(sideMenuVBoxSeparator) &&
				sideMenuVBox.getChildren().contains(timetablesPane) &&
				sideMenuVBox.getChildren().contains(filtersPane)) {
			sideMenuVBox.getChildren().add(1, sideMenuVBoxSeparator);
		} else {
			sideMenuVBox.getChildren().remove(sideMenuVBoxSeparator);
		}
	}

	@FXML
	protected void initialize() {
		timetablesToggle.setOnAction(event -> {
			if (timetablesToggle.isSelected()) {
				sideMenuVBox.getChildren().add(0, timetablesPane);
			} else {
				sideMenuVBox.getChildren().remove(timetablesPane);
			}
			adjustSideMenuSeparators();
		});

		filtersToggle.setOnAction(event -> {
			if (filtersToggle.isSelected()) {
				sideMenuVBox.getChildren().add(filtersPane);
			} else {
				sideMenuVBox.getChildren().remove(filtersPane);
			}
			adjustSideMenuSeparators();
		});
	}

}
