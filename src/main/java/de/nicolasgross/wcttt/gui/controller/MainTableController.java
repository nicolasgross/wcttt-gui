package de.nicolasgross.wcttt.gui.controller;

import de.nicolasgross.wcttt.gui.model.Model;
import de.nicolasgross.wcttt.lib.model.Room;
import de.nicolasgross.wcttt.lib.model.Semester;
import de.nicolasgross.wcttt.lib.model.TimetablePeriod;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainTableController extends SubscriberController {

	private static final List<String> WEEK_DAY_NAMES = Arrays.asList("Monday",
			"Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday");

	@FXML
	private VBox timetableDaysVBox;

	private List<TableView<TimetablePeriod>> timetableDays = new ArrayList<>();


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
		createTableViews();
		timetableDaysVBox.getChildren().setAll(timetableDays);
		createPeriodColumns();
		createRoomColumns();
	}

	private void createTableViews() {
		timetableDays.clear();
		for (int i = 0; i < getModel().getDaysPerWeek(); i++) {
			TableView<TimetablePeriod> tableView = new TableView<>();
			tableView.setPrefWidth(Region.USE_COMPUTED_SIZE);
			tableView.setPrefHeight(Region.USE_COMPUTED_SIZE);
			tableView.setEditable(false);
			tableView.setPlaceholder(new Label("No timetable selected"));
			TableColumn<TimetablePeriod, String> tableColumn =
					new TableColumn<>();
			tableColumn.setResizable(false);
			tableColumn.setSortable(false);
			tableColumn.setReorderable(false);
			tableColumn.setPrefWidth(125.0);
			tableView.getColumns().add(tableColumn);
			timetableDays.add(tableView);
		}
	}

	private void createPeriodColumns() {
		for (int i = 0; i < getModel().getDaysPerWeek(); i++) {
			TableView<TimetablePeriod> tableView = timetableDays.get(i);
			TableColumn<TimetablePeriod, ?> periodColumn =
					tableView.getColumns().get(0);
			periodColumn.setText(WEEK_DAY_NAMES.get(i));
		}
	}

	private void createRoomColumns() {
		for (Room room : getModel().getRooms()) {
			for (TableView<TimetablePeriod> tableView : timetableDays) {
				TableColumn<TimetablePeriod, String> tableColumn =
						new TableColumn<>();
				tableColumn.setText(room.getName());
				tableColumn.setResizable(false);
				tableColumn.setSortable(false);
				tableColumn.setReorderable(false);
				tableColumn.setPrefWidth(125.0);
				tableView.getColumns().add(tableColumn);
			}
		}
	}

}
