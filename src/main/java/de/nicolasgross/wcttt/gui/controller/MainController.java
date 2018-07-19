package de.nicolasgross.wcttt.gui.controller;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.TableView;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class MainController {

	@FXML
	private HBox sideMenuHBox;
	@FXML
	private Separator sideMenuHBoxSeparator;
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
		// Adjust VBox separator
		if(!sideMenuVBox.getChildren().contains(sideMenuVBoxSeparator) &&
				sideMenuVBox.getChildren().contains(timetablesPane) &&
				sideMenuVBox.getChildren().contains(filtersPane)) {
			sideMenuVBox.getChildren().add(1, sideMenuVBoxSeparator);
		} else {
			sideMenuVBox.getChildren().remove(sideMenuVBoxSeparator);
		}
		// Adjust HBox separator
		if (sideMenuVBox.getChildren().isEmpty()) {
			sideMenuHBox.getChildren().remove(sideMenuHBoxSeparator);
		} else if(!sideMenuHBox.getChildren().contains(sideMenuHBoxSeparator)) {
			sideMenuHBox.getChildren().add(1, sideMenuHBoxSeparator);
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
