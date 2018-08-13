package de.nicolasgross.wcttt.gui.model;

import de.nicolasgross.wcttt.lib.model.*;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Flow;
import java.util.concurrent.SubmissionPublisher;

public class ModelImpl implements Model {

	private static final String WCTTT = "WIAI Course Timetabling Tool";

	private Path xmlPath;
	private BooleanProperty changed;
	private Semester semester;
	private ObservableList<Teacher> teachers =
			FXCollections.observableList(new LinkedList<>());
	private SubmissionPublisher<Semester> semesterChangesNotifier =
			new SubmissionPublisher<>();
	private SubmissionPublisher<List<Timetable>> timetablesChangesNotifier =
			new SubmissionPublisher<>();
	private StringProperty title = new SimpleStringProperty();
	private StringProperty lastAction = new SimpleStringProperty();
	private StringProperty unsavedChanges = new SimpleStringProperty();
	private StringProperty stateText = new SimpleStringProperty();

	public ModelImpl() {
		Platform.runLater(() -> stateText.bind(
				Bindings.concat(lastAction, " - ", unsavedChanges)));
		changed = new SimpleBooleanProperty(true); // to trigger change listener
		changed.addListener((observable, oldValue, newValue) -> {
			if (newValue) {
				unsavedChanges.setValue("Unsaved changes");
			} else {
				unsavedChanges.setValue("No unsaved changes");
			}
		});
		setSemester(null, new SemesterImpl());
	}

	@Override
	public Optional<Path> getXmlPath() {
		return Optional.ofNullable(xmlPath);
	}

	@Override
	public void setXmlPath(Path xmlPath) {
		this.xmlPath = xmlPath;
		updateTitle();
	}

	@Override
	public BooleanProperty isChanged() {
		return changed;
	}

	public void setChanged(boolean changed) {
		this.changed.setValue(changed);
	}

	@Override
	public Semester getSemester() {
		return semester;
	}

	private final ListChangeListener<? super Teacher> teacherChangeListener =
			c -> {
				teachers.clear();
				for (Chair chair : semester.getChairs()) {
					teachers.addAll(chair.getTeachers());
				}
			};

	private void initListenToTeacherChanges() {
		for (Chair chairToListen : semester.getChairs()) {
			chairToListen.getTeachers().addListener(teacherChangeListener);
		}
	}

	private void createTeacherList() {
		teachers.clear();
		for (Chair chair : semester.getChairs()) {
			teachers.addAll(chair.getTeachers());
		}
	}

	@Override
	public void setSemester(Path xmlPath, Semester semester) {
		this.semester = semester;
		setXmlPath(xmlPath);
		setChanged(false);
		teachers.clear();
		initListenToTeacherChanges();
		createTeacherList();
		semesterChangesNotifier.submit(semester);
		timetablesChangesNotifier.submit(semester.getTimetables());
		setLastAction("Semester " + semester.getName() + " loaded");
	}

	public StringProperty getTitleProperty() {
		return title;
	}

	private void setLastAction(String info) {
		lastAction.setValue(info);
	}

	public StringProperty getStateTextProperty() {
		return stateText;
	}

	public void subscribeSemesterChanges(
			Flow.Subscriber<? super Semester> subscriber) {
		semesterChangesNotifier.subscribe(subscriber);
	}

	public void subscribeTimetablesChanges(
			Flow.Subscriber<? super List<Timetable>> subscriber) {
		timetablesChangesNotifier.subscribe(subscriber);
	}

	public void close() {
		semesterChangesNotifier.close();
		timetablesChangesNotifier.close();
	}

	@Override
	public String getName() {
		return semester.getName();
	}

	private void updateTitle() {
		// todo add more state info
		// todo use more properties
		if (xmlPath == null) {
			title.setValue(WCTTT + " - " + semester);
		} else {
			title.setValue(WCTTT + " - " + semester + " - " +
					xmlPath.toString());
		}
	}

	@Override
	public void setName(String name) {
		semester.setName(name);
		setChanged(true);
		updateTitle();
	}

	@Override
	public int getDaysPerWeek() {
		return semester.getDaysPerWeek();
	}

	@Override
	public void setDaysPerWeek(int daysPerWeek) throws WctttModelException {
		semester.setDaysPerWeek(daysPerWeek);
		setChanged(true);
		semesterChangesNotifier.submit(semester);
	}

	@Override
	public int getTimeSlotsPerDay() {
		return semester.getTimeSlotsPerDay();
	}

	@Override
	public void setTimeSlotsPerDay(int timeSlotsPerDay)
			throws WctttModelException {
		semester.setTimeSlotsPerDay(timeSlotsPerDay);
		setChanged(true);
		semesterChangesNotifier.submit(semester);
	}

	@Override
	public int getMaxDailyLecturesPerCur() {
		return semester.getMaxDailyLecturesPerCur();
	}

	@Override
	public void setMaxDailyLecturesPerCur(int maxDailyLecturesPerCur)
			throws WctttModelException {
		semester.setMaxDailyLecturesPerCur(maxDailyLecturesPerCur);
		setChanged(true);
		semesterChangesNotifier.submit(semester);
	}

	@Override
	public ConstraintWeightings getConstrWeightings() {
		return semester.getConstrWeightings();
	}

	@Override
	public void setConstrWeightings(ConstraintWeightings constrWeightings) {
		semester.setConstrWeightings(constrWeightings);
		setChanged(true);
		semesterChangesNotifier.submit(semester);
	}

	@Override
	public ObservableList<Chair> getChairs() {
		return semester.getChairs();
	}

	@Override
	public ObservableList<InternalRoom> getInternalRooms() {
		return semester.getInternalRooms();
	}

	@Override
	public ObservableList<ExternalRoom> getExternalRooms() {
		return semester.getExternalRooms();
	}

	@Override
	public ObservableList<Course> getCourses() {
		return semester.getCourses();
	}

	@Override
	public ObservableList<Curriculum> getCurricula() {
		return semester.getCurricula();
	}

	@Override
	public ObservableList<Timetable> getTimetables() {
		return semester.getTimetables();
	}

	public ObservableList<Teacher> getTeachers() {
		return teachers;
	}

	public void addChair(Chair chair) throws WctttModelException {
		semester.addChair(chair);
		chair.getTeachers().addListener(teacherChangeListener);
		if (!chair.getTeachers().isEmpty()) {
			createTeacherList();
		}
		setChanged(true);
	}

	@Override
	public boolean removeChair(Chair chair) throws WctttModelException {
		boolean existed = false;
		if ((existed = semester.removeChair(chair))) {
			setChanged(true);
		}
		return existed;
	}

	@Override
	public void updateChairId(Chair chair, String id)
			throws WctttModelException {

	}

	@Override
	public void addTeacherToChair(Teacher teacher, Chair chair)
			throws WctttModelException {
		semester.addTeacherToChair(teacher, chair);
	}

	@Override
	public boolean removeTeacherFromChair(Teacher teacher, Chair chair)
			throws WctttModelException {
		return false;
	}

	@Override
	public void updateTeacherId(Teacher teacher, Chair chair, String id)
			throws WctttModelException {

	}

	@Override
	public void addInternalRoom(InternalRoom room) throws WctttModelException {

	}

	@Override
	public void addExternalRoom(ExternalRoom room) throws WctttModelException {

	}

	@Override
	public boolean removeInternalRoom(InternalRoom room) {
		return false;
	}

	@Override
	public boolean removeExternalRoom(ExternalRoom room) {
		return false;
	}

	@Override
	public void updateRoomId(Room room, String id) throws WctttModelException {

	}

	@Override
	public void addCourse(Course course) throws WctttModelException {

	}

	@Override
	public boolean removeCourse(Course course) throws WctttModelException {
		return false;
	}

	@Override
	public void updateCourseId(Course course, String id)
			throws WctttModelException {

	}

	@Override
	public void addCourseLecture(Session lecture, Course course)
			throws WctttModelException {

	}

	@Override
	public boolean removeCourseLecture(Session lecture, Course course)
			throws WctttModelException {
		return false;
	}

	@Override
	public void addCoursePractical(Session practical, Course course)
			throws WctttModelException {

	}

	@Override
	public boolean removeCoursePractical(Session practical, Course course)
			throws WctttModelException {
		return false;
	}

	@Override
	public void updateCourseSessionId(Session session, Course course, String id)
			throws WctttModelException {

	}

	@Override
	public void addCurriculum(Curriculum curriculum)
			throws WctttModelException {

	}

	@Override
	public boolean removeCurriculum(Curriculum curriculum) {
		return false;
	}

	@Override
	public void updateCurriculumId(Curriculum curriculum, String id)
			throws WctttModelException {

	}

	@Override
	public void addTimetable(Timetable timetable) {

	}

	@Override
	public boolean removeTimetable(Timetable timetable) {
		boolean existed = semester.removeTimetable(timetable);
		if (existed) {
			setChanged(true);
			// No manual notification required because of ObservableList
		}
		return existed;
	}

	@Override
	public void removeAllTimetables() {

	}

	@Override
	public void updateTimetableName(Timetable timetable, String name)
			throws WctttModelException {
		semester.updateTimetableName(timetable, name);
		setChanged(true);
		timetablesChangesNotifier.submit(semester.getTimetables());
	}

}
