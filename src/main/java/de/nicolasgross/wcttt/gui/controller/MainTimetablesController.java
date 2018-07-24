package de.nicolasgross.wcttt.gui.controller;

import de.nicolasgross.wcttt.gui.model.Model;
import de.nicolasgross.wcttt.lib.model.Semester;
import de.nicolasgross.wcttt.lib.model.Timetable;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.stage.Stage;

public class MainTimetablesController extends SubscriberController {

	@FXML
	private ListView<Timetable> timetableList;


	@Override
	public void setup(Stage stage, Model model) {
		super.setup(stage, model);
		model.subscribe(this);
		updateGui();
	}

	@Override
	public void onNext(Semester item) {
		Platform.runLater(this::updateGui);
		getSubscription().request(1);
	}

	private void updateGui() {
		timetableList.setItems(getModel().getTimetables());
	}

}
