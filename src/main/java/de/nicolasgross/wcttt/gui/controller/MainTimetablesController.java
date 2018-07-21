package de.nicolasgross.wcttt.gui.controller;

import de.nicolasgross.wcttt.gui.model.Model;
import de.nicolasgross.wcttt.lib.model.Semester;
import de.nicolasgross.wcttt.lib.model.Timetable;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.ListView;

public class MainTimetablesController extends SubscriptionController {

	@FXML
	private ListView<Timetable> timetableList;


	@Override
	public void setup(Scene scene, Model model) {
		super.setup(scene, model);
		model.subscribe(this);
		updateGui();
	}

	@Override
	public void onNext(Semester item) {
		updateGui();
		getSubscription().request(1);
	}

	public void updateGui() {
		timetableList.setItems(getModel().getTimetables());
	}

}
