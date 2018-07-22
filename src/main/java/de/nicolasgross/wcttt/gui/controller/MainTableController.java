package de.nicolasgross.wcttt.gui.controller;

import de.nicolasgross.wcttt.gui.model.Model;
import de.nicolasgross.wcttt.lib.model.Semester;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.stage.Stage;

public class MainTableController extends SubscriptionController {

	@FXML
	private TableView table;


	@FXML
	protected void initialize() {
		table.setPlaceholder(new Label("No timetable selected"));
	}

	@Override
	public void setup(Stage stage, Model model) {
		super.setup(stage, model);
		model.subscribe(this);
		updateGui();
	}

	@Override
	public void onNext(Semester item) {
		updateGui();
		getSubscription().request(1);
	}

	public void updateGui() {
		// TODO update table, clear?

	}

}
