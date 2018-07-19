package de.nicolasgross.wcttt.gui.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;

public class MainTableController {

	@FXML
	private TableView tableView;

	@FXML
	protected void initialize() {
		tableView.setPlaceholder(new Label("No timetable selected"));
	}
}
