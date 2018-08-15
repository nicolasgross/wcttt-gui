package de.nicolasgross.wcttt.gui.model;

import de.nicolasgross.wcttt.lib.model.Semester;
import de.nicolasgross.wcttt.lib.model.Teacher;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;

import java.nio.file.Path;
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

	void subscribeSemesterChanges(Flow.Subscriber<? super Boolean> subscriber);

	void subscribeTimetablesChanges(
				Flow.Subscriber<? super Boolean> subscriber);

	void close();
}
