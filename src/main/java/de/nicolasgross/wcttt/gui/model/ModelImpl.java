package de.nicolasgross.wcttt.gui.model;

import de.nicolasgross.wcttt.lib.model.*;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

import java.nio.file.Path;
import java.util.LinkedList;
import java.util.Optional;
import java.util.concurrent.Flow;
import java.util.concurrent.SubmissionPublisher;

public class ModelImpl implements Model {

	private Path xmlPath;
	private boolean unchanged;
	private Semester semester;
	private ObservableList<Teacher> teachers;
	private SubmissionPublisher<Semester> newSemesterNotifier;


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

	public ModelImpl() {
		unchanged = true;
		xmlPath = null;
		semester = new SemesterImpl();
		teachers = FXCollections.observableList(new LinkedList<>());
		newSemesterNotifier = new SubmissionPublisher<>();
	}

	@Override
	public Optional<Path> getXmlPath() {
		return Optional.ofNullable(xmlPath);
	}

	@Override
	public boolean isUnchanged() {
		return unchanged;
	}

	@Override
	public void setSemester(Path xmlPath, Semester semester) {
		this.xmlPath = xmlPath;
		this.semester = semester;
		unchanged = true;
		teachers.clear();
		initListenToTeacherChanges();
		createTeacherList();
		newSemesterNotifier.submit(semester);
	}

	@Override
	public void subscribe(Flow.Subscriber<? super Semester> subscriber) {
		newSemesterNotifier.subscribe(subscriber);
	}

	@Override
	public String getName() {
		return semester.getName();
	}

	@Override
	public void setName(String name) {
		semester.setName(name);
	}

	@Override
	public int getDaysPerWeek() {
		return 0;
	}

	@Override
	public void setDaysPerWeek(int daysPerWeek) throws WctttModelException {

	}

	@Override
	public int getTimeSlotsPerDay() {
		return 0;
	}

	@Override
	public void setTimeSlotsPerDay(int timeSlotsPerDay) throws WctttModelException {

	}

	@Override
	public int getMaxDailyLecturesPerCur() {
		return 0;
	}

	@Override
	public void setMaxDailyLecturesPerCur(int maxDailyLecturesPerCur) throws WctttModelException {

	}

	@Override
	public ConstraintWeightings getConstrWeightings() {
		return null;
	}

	@Override
	public void setConstrWeightings(ConstraintWeightings constrWeightings) {

	}

	@Override
	public ObservableList<Chair> getChairs() {
		return semester.getChairs();
	}

	@Override
	public ObservableList<Room> getRooms() {
		return null;
	}

	@Override
	public ObservableList<Course> getCourses() {
		return null;
	}

	@Override
	public ObservableList<Curriculum> getCurricula() {
		return semester.getCurricula();
	}

	@Override
	public ObservableList<Timetable> getTimetables() {
		return null;
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
		unchanged = false;
	}

	@Override
	public boolean removeChair(Chair chair) throws WctttModelException {
		return false;
	}

	@Override
	public void updateChairId(Chair chair, String id) throws WctttModelException {

	}

	@Override
	public void addTeacherToChair(Teacher teacher, Chair chair) throws WctttModelException {
		semester.addTeacherToChair(teacher, chair);
	}

	@Override
	public boolean removeTeacherFromChair(Teacher teacher, Chair chair) throws WctttModelException {
		return false;
	}

	@Override
	public void updateTeacherId(Teacher teacher, Chair chair, String id) throws WctttModelException {

	}

	@Override
	public void addRoom(Room room) throws WctttModelException {

	}

	@Override
	public boolean removeRoom(Room room) {
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
	public void updateCourseId(Course course, String id) throws WctttModelException {

	}

	@Override
	public void addCourseLecture(Session lecture, Course course) throws WctttModelException {

	}

	@Override
	public boolean removeCourseLecture(Session lecture, Course course) throws WctttModelException {
		return false;
	}

	@Override
	public void addCoursePractical(Session practical, Course course) throws WctttModelException {

	}

	@Override
	public boolean removeCoursePractical(Session practical, Course course) throws WctttModelException {
		return false;
	}

	@Override
	public void updateCourseSessionId(Session session, Course course, String id) throws WctttModelException {

	}

	@Override
	public void addCurriculum(Curriculum curriculum) throws WctttModelException {

	}

	@Override
	public boolean removeCurriculum(Curriculum curriculum) {
		return false;
	}

	@Override
	public void updateCurriculumId(Curriculum curriculum, String id) throws WctttModelException {

	}

	@Override
	public void addTimetable(Timetable timetable) {

	}

	@Override
	public boolean removeTimetable(Timetable timetable) {
		return false;
	}

	@Override
	public void removeAllTimetables() {

	}

}
