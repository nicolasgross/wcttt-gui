package de.nicolasgross.wcttt.gui.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;

public class MainTableController extends Controller {

	@FXML
	private TableView table;

	@FXML
	protected void initialize() {
		table.setPlaceholder(new Label("No timetable selected"));
	}
}
