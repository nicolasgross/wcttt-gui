/*
 * WCT³ (WIAI Course Timetabling Tool) is a software that strives to automate
 * the timetabling process at the WIAI faculty of the University of Bamberg.
 *
 * WCT³-GUI comprises functionality to view generated timetables, edit semester
 * data and to generate new timetables.
 *
 * Copyright (C) 2018 Nicolas Gross
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package wcttt.gui.model;

import wcttt.gui.WctttGuiFatalException;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import wcttt.lib.model.*;

import java.nio.file.Path;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Flow;
import java.util.concurrent.SubmissionPublisher;

public class ModelImpl implements Model {

	private static final String WCTTT = "WIAI Course Timetabling Tool";
	private static final String SEMESTER_LOADED = "Semester loaded successfully";
	private static final String SEMESTER_UPDATED = "Semester data were updated";
	private static final String COURSES_UPDATED = "Course data were updated";
	private static final String ROOMS_UPDATED = "Room data were updated";
	private static final String CHAIRS_UPDATED = "Chair data were updated";
	private static final String CURRICULA_UPDATED = "Curriculum data were updated";
	private static final String TIMETABLES_UPDATED = "Timetable data were updated";

	private Path xmlPath;
	private BooleanProperty changed;
	private Semester semester;
	private ObservableList<Teacher> teachers =
			FXCollections.observableList(new LinkedList<>());
	private SubmissionPublisher<Boolean> semesterChangesNotifier =
			new SubmissionPublisher<>();
	private SubmissionPublisher<Boolean> timetablesChangesNotifier =
			new SubmissionPublisher<>();
	private StringProperty title = new SimpleStringProperty();

	private StringProperty lastAction = new SimpleStringProperty();
	private StringProperty unsavedChanges = new SimpleStringProperty();
	private StringProperty stateText = new SimpleStringProperty();

	private long nextChairId = 0;
	private long nextTeacherId = 0;
	private long nextRoomId = 0;
	private long nextCourseId = 0;
	private long nextSessionId = 0;
	private long nextCurriculumId = 0;
	private long nextTimetablName = 0;

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

	@Override
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
				teachers.sort(Comparator.comparing(Teacher::getName));
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
			teachers.sort(Comparator.comparing(Teacher::getName));
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
		resetIds();
		getChairs().sort(Comparator.comparing(Chair::getAbbreviation));
		getInternalRooms().sort(Comparator.comparing(InternalRoom::getName));
		getExternalRooms().sort(Comparator.comparing(ExternalRoom::getName));
		getCourses().sort(Comparator.comparing(Course::getAbbreviation));
		getCurricula().sort(Comparator.comparing(Curriculum::getName));
		semesterChangesNotifier.submit(true);
		timetablesChangesNotifier.submit(true);
		setLastAction(SEMESTER_LOADED);
	}

	private void resetIds() {
		nextChairId = 0;
		nextTeacherId = 0;
		nextRoomId = 0;
		nextCourseId = 0;
		nextSessionId = 0;
		nextCurriculumId = 0;
		nextTimetablName = 0;
	}

	@Override
	public StringProperty getTitleProperty() {
		return title;
	}

	private void setLastAction(String info) {
		lastAction.setValue(info);
	}

	@Override
	public StringProperty getStateTextProperty() {
		return stateText;
	}

	@Override
	public void subscribeSemesterChanges(
			Flow.Subscriber<? super Boolean> subscriber) {
		semesterChangesNotifier.subscribe(subscriber);
	}

	@Override
	public void subscribeTimetablesChanges(
			Flow.Subscriber<? super Boolean> subscriber) {
		timetablesChangesNotifier.subscribe(subscriber);
	}

	@Override
	public void close() {
		semesterChangesNotifier.close();
		timetablesChangesNotifier.close();
	}

	@Override
	public String getName() {
		return semester.getName();
	}

	private void updateTitle() {
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
		setLastAction(SEMESTER_UPDATED);
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
		setLastAction(SEMESTER_UPDATED);
		semesterChangesNotifier.submit(false);
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
		setLastAction(SEMESTER_UPDATED);
		semesterChangesNotifier.submit(false);
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
		setLastAction(SEMESTER_UPDATED);
		semesterChangesNotifier.submit(false);
	}

	@Override
	public ConstraintWeightings getConstrWeightings() {
		return semester.getConstrWeightings();
	}

	@Override
	public void setConstrWeightings(ConstraintWeightings constrWeightings) {
		semester.setConstrWeightings(constrWeightings);
		setChanged(true);
		setLastAction(SEMESTER_UPDATED);
		semesterChangesNotifier.submit(false);
		timetablesChangesNotifier.submit(false);
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

	@Override
	public ObservableList<Teacher> getTeachers() {
		return teachers;
	}

	@Override
	public List<Period> getPeriods() {
		List<Period> periods = new LinkedList<>();
		for (int i = 0; i < getDaysPerWeek(); i++) {
			for (int j = 0; j < getTimeSlotsPerDay(); j++) {
				try {
					periods.add(new Period(i + 1, j + 1));
				} catch (WctttModelException e) {
					throw new WctttGuiFatalException("Implementation error, " +
							"created a period with invalid parameters", e);
				}
			}
		}
		return periods;
	}

	private void setNextChairId(Chair chair) {
		while (true) {
			try {
				semester.updateChairId(chair, "chair" + nextChairId);
				nextChairId++;
				return;
			} catch (WctttModelException e) {
				nextChairId++;
			}
		}
	}

	private void setNextTeacherId(Teacher teacher, Chair chair) {
		while (true) {
			try {
				semester.updateTeacherId(teacher, chair,
						"teacher" + nextTeacherId);
				nextTeacherId++;
				return;
			} catch (WctttModelException e) {
				nextTeacherId++;
			}
		}
	}

	private void setNextRoomId(Room room) {
		while (true) {
			try {
				semester.updateRoomId(room, "room" + nextRoomId);
				nextRoomId++;
				return;
			} catch (WctttModelException e) {
				nextRoomId++;
			}
		}
	}

	private void setNextCourseId(Course course) {
		while (true) {
			try {
				semester.updateCourseId(course, "course" + nextCourseId);
				nextCourseId++;
				return;
			} catch (WctttModelException e) {
				nextCourseId++;
			}
		}
	}

	private void setNextSessionId(Session session, Course course) {
		while (true) {
			try {
				semester.updateCourseSessionId(session, course,
						"session" + nextSessionId);
				nextSessionId++;
				return;
			} catch (WctttModelException e) {
				nextSessionId++;
			}
		}
	}

	private void setNextCurriculumId(Curriculum curriculum) {
		while (true) {
			try {
				semester.updateCurriculumId(curriculum,
						"curriculum" + nextCurriculumId);
				nextCurriculumId++;
				return;
			} catch (WctttModelException e) {
				nextCurriculumId++;
			}
		}
	}

	private void setNextTimetableName(Timetable timetable) {
		while (true) {
			try {
				semester.updateTimetableName(timetable,
						"timetable" + nextTimetablName);
				nextTimetablName++;
				return;
			} catch (WctttModelException e) {
				nextTimetablName++;
			}
		}
	}

	@Override
	public void addChair(Chair chair) throws WctttModelException {
		chair.setId("wcttt-gui-default-id");
		semester.addChair(chair);
		setNextChairId(chair);
		getChairs().sort(Comparator.comparing(Chair::getAbbreviation));
		chair.getTeachers().addListener(teacherChangeListener);
		if (!chair.getTeachers().isEmpty()) {
			createTeacherList();
		}
		setChanged(true);
		setLastAction(CHAIRS_UPDATED);
		semesterChangesNotifier.submit(true);
	}

	@Override
	public boolean removeChair(Chair chair) throws WctttModelException {
		boolean existed = false;
		if ((existed = semester.removeChair(chair))) {
			setChanged(true);
			setLastAction(CHAIRS_UPDATED);
			semesterChangesNotifier.submit(true);
		}
		return existed;
	}

	@Override
	public void updateChairId(Chair chair, String id)
			throws WctttModelException {
		semester.updateChairId(chair, id);
	}

	@Override
	public void updateChairData(Chair chair, String name, String abbreviation)
			throws WctttModelException {
		semester.updateChairData(chair, name, abbreviation);
		getChairs().sort(Comparator.comparing(Chair::getAbbreviation));
		setChanged(true);
		setLastAction(CHAIRS_UPDATED);
		semesterChangesNotifier.submit(false);
	}

	@Override
	public void addTeacherToChair(Teacher teacher, Chair chair)
			throws WctttModelException {
		teacher.setId("wcttt-gui-default-id");
		semester.addTeacherToChair(teacher, chair);
		setNextTeacherId(teacher, chair);
		setChanged(true);
		setLastAction(CHAIRS_UPDATED);
		semesterChangesNotifier.submit(true);
	}

	@Override
	public boolean removeTeacherFromChair(Teacher teacher, Chair chair)
			throws WctttModelException {
		boolean existed = false;
		if ((existed = semester.removeTeacherFromChair(teacher, chair))) {
			setChanged(true);
			setLastAction(CHAIRS_UPDATED);
			semesterChangesNotifier.submit(true);
		}
		return existed;
	}

	@Override
	public void updateTeacherId(Teacher teacher, Chair chair, String id)
			throws WctttModelException {
		semester.updateTeacherId(teacher, chair, id);
	}

	@Override
	public void updateTeacherData(Teacher teacher, String name,
	                              List<Period> unfavorablePeriods,
	                              List<Period> unavailablePeriods)
			throws WctttModelException {
		semester.updateTeacherData(teacher, name, unfavorablePeriods,
				unavailablePeriods);
		teachers.sort(Comparator.comparing(Teacher::getName));
		setChanged(true);
		setLastAction(COURSES_UPDATED);
		semesterChangesNotifier.submit(false);
	}

	@Override
	public void addInternalRoom(InternalRoom room) throws WctttModelException {
		room.setId("wcttt-gui-default-id");
		semester.addInternalRoom(room);
		setNextRoomId(room);
		getInternalRooms().sort(Comparator.comparing(InternalRoom::getName));
		setChanged(true);
		setLastAction(ROOMS_UPDATED);
		semesterChangesNotifier.submit(true);
	}

	@Override
	public void addExternalRoom(ExternalRoom room) throws WctttModelException {
		room.setId("wcttt-gui-default-id");
		semester.addExternalRoom(room);
		setNextRoomId(room);
		getExternalRooms().sort(Comparator.comparing(ExternalRoom::getName));
		setChanged(true);
		setLastAction(ROOMS_UPDATED);
		semesterChangesNotifier.submit(true);
	}

	@Override
	public boolean removeInternalRoom(InternalRoom room)
			throws WctttModelException {
		boolean existed = false;
		if ((existed = semester.removeInternalRoom(room))) {
			setChanged(true);
			setLastAction(ROOMS_UPDATED);
			semesterChangesNotifier.submit(true);
		}
		return existed;
	}

	@Override
	public boolean removeExternalRoom(ExternalRoom room)
			throws WctttModelException {
		boolean existed = false;
		if ((existed = semester.removeExternalRoom(room))) {
			setChanged(true);
			setLastAction(ROOMS_UPDATED);
			semesterChangesNotifier.submit(true);
		}
		return existed;
	}

	@Override
	public void updateRoomId(Room room, String id) throws WctttModelException {
		semester.updateRoomId(room, id);
	}

	@Override
	public void updateInternalRoomData(InternalRoom room, String name,
	                                   int capacity, RoomFeatures features)
			throws WctttModelException {
		semester.updateInternalRoomData(room, name, capacity, features);
		getInternalRooms().sort(Comparator.comparing(InternalRoom::getName));
		setChanged(true);
		setLastAction(ROOMS_UPDATED);
		semesterChangesNotifier.submit(false);
	}

	@Override
	public void updateExternalRoomData(ExternalRoom room, String name)
			throws WctttModelException {
		semester.updateExternalRoomData(room, name);
		getExternalRooms().sort(Comparator.comparing(ExternalRoom::getName));
		setChanged(true);
		setLastAction(ROOMS_UPDATED);
		semesterChangesNotifier.submit(false);
	}

	@Override
	public void addCourse(Course course) throws WctttModelException {
		course.setId("wcttt-gui-default-id");
		if (!getChairs().isEmpty() && course.getChair().equals(new Chair())) {
			course.setChair(getChairs().get(0));
		}
		semester.addCourse(course);
		setNextCourseId(course);
		getCourses().sort(Comparator.comparing(Course::getAbbreviation));
		setChanged(true);
		setLastAction(COURSES_UPDATED);
		semesterChangesNotifier.submit(true);
	}

	@Override
	public boolean removeCourse(Course course) throws WctttModelException {
		boolean existed = false;
		if ((existed = semester.removeCourse(course))) {
			setChanged(true);
			setLastAction(COURSES_UPDATED);
			semesterChangesNotifier.submit(true);
		}
		return existed;
	}

	@Override
	public void updateCourseId(Course course, String id)
			throws WctttModelException {
		semester.updateCourseId(course, id);
	}

	@Override
	public void updateCourseData(Course course, String name, String abbreviation,
	                             Chair chair, CourseLevel courseLevel,
	                             int minNumberOfDays)
			throws WctttModelException {
		semester.updateCourseData(course, name, abbreviation, chair, courseLevel,
				minNumberOfDays);
		getCourses().sort(Comparator.comparing(Course::getAbbreviation));
		setChanged(true);
		setLastAction(COURSES_UPDATED);
		semesterChangesNotifier.submit(false);
	}

	@Override
	public void addCourseLecture(Session lecture, Course course)
			throws WctttModelException {
		lecture.setId("wcttt-gui-default-id");
		if (!getTeachers().isEmpty() &&
				lecture.getTeacher().equals(new Teacher())) {
			lecture.setTeacher(getTeachers().get(0));
		}
		semester.addCourseLecture(lecture, course);
		setNextSessionId(lecture, course);
		setChanged(true);
		setLastAction(COURSES_UPDATED);
		semesterChangesNotifier.submit(true);
	}

	@Override
	public boolean removeCourseLecture(Session lecture)
			throws WctttModelException {
		boolean existed = false;
		if ((existed = semester.removeCourseLecture(lecture))) {
			setChanged(true);
			setLastAction(COURSES_UPDATED);
			semesterChangesNotifier.submit(true);
		}
		return existed;
	}

	@Override
	public void addCoursePractical(Session practical, Course course)
			throws WctttModelException {
		practical.setId("wcttt-gui-default-id");
		if (!getTeachers().isEmpty() &&
				practical.getTeacher().equals(new Teacher())) {
			practical.setTeacher(getTeachers().get(0));
		}
		semester.addCoursePractical(practical, course);
		setNextSessionId(practical, course);
		setChanged(true);
		setLastAction(COURSES_UPDATED);
		semesterChangesNotifier.submit(true);
	}

	@Override
	public boolean removeCoursePractical(Session practical)
			throws WctttModelException {
		boolean existed = false;
		if ((existed = semester.removeCoursePractical(practical))) {
			setChanged(true);
			setLastAction(COURSES_UPDATED);
			semesterChangesNotifier.submit(true);
		}
		return existed;
	}

	@Override
	public void updateCourseSessionId(Session session, Course course, String id)
			throws WctttModelException {
		semester.updateCourseSessionId(session, course, id);
	}

	@Override
	public void updateInternalSessionData(InternalSession session, String name,
	                                      Teacher teacher, boolean doubleSession,
	                                      Period preAssignment, int students,
	                                      RoomFeatures roomRequirements)
			throws WctttModelException {
		semester.updateInternalSessionData(session, name, teacher, doubleSession,
				preAssignment, students, roomRequirements);
		setChanged(true);
		setLastAction(COURSES_UPDATED);
		semesterChangesNotifier.submit(false);
	}

	@Override
	public void updateExternalSessionData(ExternalSession session, String name,
	                                      Teacher teacher, boolean doubleSession,
	                                      Period preAssignment, ExternalRoom room)
			throws WctttModelException {
		semester.updateExternalSessionData(session, name, teacher, doubleSession,
				preAssignment, room);
		setChanged(true);
		setLastAction(COURSES_UPDATED);
		semesterChangesNotifier.submit(false);
	}

	@Override
	public void addCurriculum(Curriculum curriculum)
			throws WctttModelException {
		curriculum.setId("wcttt-gui-default-id");
		semester.addCurriculum(curriculum);
		setNextCurriculumId(curriculum);
		getCurricula().sort(Comparator.comparing(Curriculum::getName));
		setChanged(true);
		setLastAction(CURRICULA_UPDATED);
		semesterChangesNotifier.submit(true);
	}

	@Override
	public boolean removeCurriculum(Curriculum curriculum)
			throws WctttModelException {
		boolean existed = false;
		if ((existed = semester.removeCurriculum(curriculum))) {
			setChanged(true);
			setLastAction(CURRICULA_UPDATED);
			semesterChangesNotifier.submit(true);
		}
		return existed;
	}

	@Override
	public void updateCurriculumId(Curriculum curriculum, String id)
			throws WctttModelException {
		semester.updateCurriculumId(curriculum, id);
	}


	@Override
	public void updateCurriculumData(Curriculum curriculum, String name,
	                                 List<Course> courses)
			throws WctttModelException {
		semester.updateCurriculumData(curriculum, name, courses);
		getCurricula().sort(Comparator.comparing(Curriculum::getName));
		setChanged(true);
		setLastAction(CURRICULA_UPDATED);
		semesterChangesNotifier.submit(false);
	}

	@Override
	public void addTimetable(Timetable timetable) throws WctttModelException {
		timetable.setName("wcttt-gui-default-id");
		semester.addTimetable(timetable);
		setNextTimetableName(timetable);
		setChanged(true);
		setLastAction(TIMETABLES_UPDATED);
		timetablesChangesNotifier.submit(false);
	}

	@Override
	public boolean removeTimetable(Timetable timetable) {
		boolean existed = semester.removeTimetable(timetable);
		if (existed) {
			setChanged(true);
			setLastAction(TIMETABLES_UPDATED);
			timetablesChangesNotifier.submit(false);
		}
		return existed;
	}

	@Override
	public void updateTimetableName(Timetable timetable, String name)
			throws WctttModelException {
		semester.updateTimetableName(timetable, name);
		setChanged(true);
		setLastAction(TIMETABLES_UPDATED);
		timetablesChangesNotifier.submit(false);
	}
}
