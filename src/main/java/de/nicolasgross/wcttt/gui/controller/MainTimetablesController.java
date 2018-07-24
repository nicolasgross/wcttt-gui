package de.nicolasgross.wcttt.gui.controller;

import de.nicolasgross.wcttt.gui.model.Model;
import de.nicolasgross.wcttt.lib.model.Semester;
import de.nicolasgross.wcttt.lib.model.Timetable;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;

public class MainTimetablesController extends SubscriberController {

	@FXML
	private TableView<Timetable> timetableSelectionTable;
	@FXML
	private TableColumn<Timetable, String> timetableColumn;
	@FXML
	private TableColumn<Timetable, Double> penaltyColumn;

	@FXML
	protected void initialize() {
		timetableSelectionTable.setPlaceholder(new Label("No timetables " +
				"generated for this semester"));
	}

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
		timetableSelectionTable.setItems(getModel().getTimetables());
	}

}
