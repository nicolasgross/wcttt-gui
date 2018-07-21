package de.nicolasgross.wcttt.gui.model;

import de.nicolasgross.wcttt.lib.model.Semester;
import de.nicolasgross.wcttt.lib.model.Teacher;
import javafx.collections.ObservableList;

import java.nio.file.Path;
import java.util.Optional;
import java.util.concurrent.Flow;

public interface Model extends Semester, Flow.Publisher<Semester> {

	Optional<Path> getXmlPath();

	boolean isUnchanged();

	void setSemester(Path xmlPath, Semester semester);

	public ObservableList<Teacher> getTeachers();

}
