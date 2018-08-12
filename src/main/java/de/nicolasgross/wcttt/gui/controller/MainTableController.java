package de.nicolasgross.wcttt.gui.controller;

import de.nicolasgross.wcttt.gui.WctttGuiFatalException;
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
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class MainTableController extends SubscriberController<Semester> {

	private static final List<String> WEEK_DAY_NAMES = Arrays.asList("Monday",
			"Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday");
	private static final List<String> TIME_SLOT_NAMES = Arrays.asList(
			"  8:15 -   9:45", "10:15 - 11:45", "12:15 - 13:45",
			"14:15 - 15:45", "16:15 - 17:45", "18:15 - 19:45", "20:15 - 21:45");

	@FXML
	private VBox timetableDaysVBox;

	private List<TableView<TimetablePeriod>> timetableDays = new ArrayList<>();
	private Timetable selectedTimetable = null;
	private Teacher teacherFilter = null;
	private Chair chairFilter = null;
	private Course courseFilter = null;
	private Curriculum curriculumFilter = null;


	@Override
	public void setup(Stage stage, Model model, MainController mainController) {
		super.setup(stage, model, mainController);
		model.subscribeSemesterChanges(this);
		Platform.runLater(this::updateGui);
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
		createRoomColumns(getModel().getInternalRooms());
		createRoomColumns(getModel().getExternalRooms());
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
			@SuppressWarnings("unchecked")
			TableColumn<TimetablePeriod, String> periodColumn =
					(TableColumn<TimetablePeriod, String>) tableView.
							getColumns().get(0);
			periodColumn.setText(WEEK_DAY_NAMES.get(i));
			periodColumn.setCellValueFactory(param ->
					new SimpleStringProperty(TIME_SLOT_NAMES.get(
							param.getValue().getTimeSlot() - 1)));
		}
	}

	private void createRoomColumns(List<? extends Room> rooms) {
		for (Room room : rooms) {
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
						if (assignment.getRoom().getId().equals(
										param.getTableColumn().getId())) {
							return new SimpleStringProperty(
									assignment.getSession().toString());
						}
					}
					return new SimpleStringProperty("");
				});
				tableView.getColumns().add(tableColumn);
			}
		}
	}

	private boolean filtersActive() {
		return teacherFilter != null || chairFilter != null ||
				courseFilter != null || curriculumFilter != null;
	}

	private boolean filtersUnchanged(Teacher teacher, Chair chair,
	                                 Course course, Curriculum curriculum) {
		return teacher == teacherFilter && chair == chairFilter &&
				course == courseFilter && curriculum == curriculumFilter;
	}

	private void updateSelectedFilters(Teacher teacher, Chair chair,
	                                   Course course, Curriculum curriculum) {
		teacherFilter = teacher;
		chairFilter = chair;
		courseFilter = course;
		curriculumFilter = curriculum;
	}

	void setTimetable(Timetable timetable) {
		selectedTimetable = timetable;
		Platform.runLater(() -> {
			if (filtersActive()) {
				filter(teacherFilter, chairFilter, courseFilter,
						curriculumFilter);
			} else {
				setTableData(timetable);
			}
		});
	}

	private void setTableData(Timetable timetable) {
		if (timetable == null) {
			for (TableView<TimetablePeriod> tableView : timetableDays) {
				tableView.setItems(FXCollections.observableArrayList());
			}
		} else {
			for (int i = 0; i < getModel().getDaysPerWeek(); i++) {
				timetableDays.get(i).setItems(
						timetable.getDays().get(i).getPeriods());
			}
		}
	}

	void filter(Teacher teacher, Chair chair, Course course,
	            Curriculum curriculum) {
		if (filtersUnchanged(teacher, chair, course, curriculum)) {
			return;
		}
		updateSelectedFilters(teacher, chair, course, curriculum);
		if (selectedTimetable == null) {
			return;
		} else if (teacher == null && chair == null && course == null &&
				curriculum == null) {
			setTableData(selectedTimetable);
			return;
		}

		setTableData(createFilteredTimetable());
	}

	private Timetable createFilteredTimetable() {
		Timetable filteredTimetable = new Timetable("filteredTimetable");
		for (TimetableDay originalDay : selectedTimetable.getDays()) {
			try {
				TimetableDay filteredDay = new TimetableDay(originalDay.getDay());
				for (TimetablePeriod originalPeriod : originalDay.getPeriods()) {
					TimetablePeriod filteredPeriod = new TimetablePeriod(
							originalPeriod.getDay(), originalPeriod.getTimeSlot());
					addFilteredAssignments(originalPeriod, filteredPeriod);
					filteredDay.addPeriod(filteredPeriod);
				}
				filteredTimetable.addDay(filteredDay);
			} catch (WctttModelException e) {
				throw new WctttGuiFatalException(
						"Implementation error in day/time slot numbering", e);
			}
		}
		return filteredTimetable;
	}

	private void addFilteredAssignments(TimetablePeriod original,
	                                    TimetablePeriod filtered) {
		Predicate<TimetableAssignment> teacherCheck = t -> {
			if (teacherFilter != null) {
				return t.getSession().getTeacher().equals(teacherFilter);
			} else {
				return true;
			}
		};
		Predicate<TimetableAssignment> chairCheck = t -> {
			if (chairFilter != null) {
				return t.getSession().getCourse().getChair().equals(chairFilter);
			} else {
				return true;
			}
		};
		Predicate<TimetableAssignment> courseCheck = t -> {
			if (courseFilter != null) {
				return t.getSession().getCourse().equals(courseFilter);
			} else {
				return true;
			}
		};
		Predicate<TimetableAssignment> curriculumCheck = t -> {
			if (curriculumFilter != null) {
				for (Course course : curriculumFilter.getCourses()) {
					if (t.getSession().getCourse().equals(course)) {
						return true;
					}
				}
				return false;
			} else {
				return true;
			}
		};
		Predicate<TimetableAssignment> combinedFilter = teacherCheck.and(
				chairCheck.and(courseCheck.and(curriculumCheck)));

		filtered.getAssignments().setAll(original.getAssignments().stream().
				filter(combinedFilter).collect(Collectors.toList()));
	}
}
