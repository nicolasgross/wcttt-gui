package de.nicolasgross.wcttt.gui.model;

import de.nicolasgross.wcttt.lib.model.*;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;

import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Flow;

public interface Model extends Semester {

	Optional<Path> getXmlPath();

	void setXmlPath(Path xmlPath);

	BooleanProperty isChanged();

	void setChanged(boolean changed);

	Semester getSemester();

	void setSemester(Path xmlPath, Semester semester);

	ObservableList<Teacher> getTeachers();

	StringProperty getTitleProperty();

	StringProperty getStateTextProperty();

	void subscribeSemesterChanges(Flow.Subscriber<? super Semester> subscriber);

	void subscribeTimetablesChanges(
				Flow.Subscriber<? super List<Timetable>> subscriber);

	void close();
}
