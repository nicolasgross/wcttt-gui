package de.nicolasgross.wcttt.gui.controller;

import de.nicolasgross.wcttt.gui.model.Model;
import de.nicolasgross.wcttt.lib.model.Timetable;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;

public class MainTimetablesController extends Controller {

	@FXML
	private ListView<Timetable> timetableList;


	@Override
	public void setModel(Model model) {
		super.setModel(model);
		timetableList.setItems(getModel().getTimetables());
	}
}
