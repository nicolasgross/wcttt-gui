package de.nicolasgross.wcttt.gui.controller;

import de.nicolasgross.wcttt.gui.model.Model;
import de.nicolasgross.wcttt.lib.model.*;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
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
	private static final List<String> TIME_SLOT_NAMES = Arrays.asList(
			"  8:15 -   9:45", "10:15 - 11:45", "12:15 - 13:45", "14:15 - 15:45",
			"16:15 - 17:45", "18:15 - 19:45", "20:15 - 21:45");

	@FXML
	private VBox timetableDaysVBox;

	private List<TableView<TimetablePeriod>> timetableDays = new ArrayList<>();


	@Override
	public void setup(Stage stage, Model model, MainController mainController) {
		super.setup(stage, model, mainController);
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
			tableView.setPrefHeight(196);
			tableView.setEditable(false);
			tableView.setPlaceholder(new Label("No timetable selected"));
			tableView.setSelectionModel(null);

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
			TableColumn<TimetablePeriod, String> periodColumn =
					(TableColumn<TimetablePeriod, String>) tableView.
							getColumns().get(0);
			periodColumn.setText(WEEK_DAY_NAMES.get(i));
			periodColumn.setCellValueFactory(param ->
					new SimpleStringProperty(TIME_SLOT_NAMES.get(
							param.getValue().getTimeSlot() - 1)));
		}
	}

	private void createRoomColumns() {
		for (Room room : getModel().getRooms()) {
			for (TableView<TimetablePeriod> tableView : timetableDays) {
				TableColumn<TimetablePeriod, String> tableColumn =
						new TableColumn<>();
				tableColumn.setText(room.getName());
				tableColumn.setId(room.getId());
				tableColumn.setResizable(false);
				tableColumn.setSortable(false);
				tableColumn.setReorderable(false);
				tableColumn.setPrefWidth(125.0);
				tableColumn.setCellValueFactory(param -> {
					for (TimetableAssignment assignment : param.getValue().
							getAssignments()) {
						if (assignment.getRoom().isPresent() &&
								(assignment.getRoom().get().getId().equals(
										param.getTableColumn().getId()))) {
							return new SimpleStringProperty(
									assignment.getSession().toString());
							// TODO external
						}
					}
					return new SimpleStringProperty("");
				});
				tableView.getColumns().add(tableColumn);
			}
		}
	}

	void setTimetable(Timetable timetable) {
		Platform.runLater(() -> {
			if (timetable == null) {
				for (TableView<TimetablePeriod> tableView : timetableDays) {
					tableView.setItems(FXCollections.observableArrayList());
				}
			} else {
				for (int i = 0; i < getModel().getDaysPerWeek(); i++) {
					timetableDays.get(i).setItems(timetable.getDays().get(i).
							getPeriods());
				}
			}
		});
	}

}
