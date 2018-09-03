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

import wcttt.lib.model.Period;
import wcttt.lib.model.Semester;
import wcttt.lib.model.Teacher;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;

import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Flow;

/**
 * The model in the MVC architecture used.
 *
 * Instead of using the CRUD methods on the semester returned by getSemester(),
 * please use the CRUD methods provided by this model/semester. These methods
 * ensure the consistency of the model.
 */
public interface Model extends Semester {

	/**
	 * Getter for the path of the currently loaded semester.
	 *
	 * @return an Optional containing the path if the current semester is loaded
	 * from a file, otherwise an empty {@code Optional}.
	 */
	Optional<Path> getXmlPath();

	/**
	 * Setter for the path to the XML file of the currently loaded semester.
	 *
	 * @param xmlPath the new path, can be {@code null}.
	 */
	void setXmlPath(Path xmlPath);

	/**
	 * Indicates whether the currently loaded semester has changes that are not
	 * already saved to the respective XML file.
	 *
	 * @return whether there are unsaved changes.
	 */
	BooleanProperty isChanged();

	/**
	 * Setter for the indicator of unsaved changes.
	 *
	 * @param changed the new status of unsaved changes.
	 */
	void setChanged(boolean changed);

	/**
	 * Getter for the currently loaded semester.
	 *
	 * @return the currently loaded semester.
	 */
	Semester getSemester();

	/**
	 * Setter for the loaded semester.
	 *
	 * @param xmlPath the path to the XML file of the new semester.
	 * @param semester the new semester.
	 */
	void setSemester(Path xmlPath, Semester semester);

	/**
	 * Getter for the list of teachers of the semester.
	 *
	 * @return the list of teachers of the semester.
	 */
	ObservableList<Teacher> getTeachers();

	/**
	 * Getter for the list of periods of the semester.
	 *
	 * @return the list of periods of the semester.
	 */
	List<Period> getPeriods();

	/**
	 * Getter for the window title text property.
	 *
	 * @return the window title text property.
	 */
	StringProperty getTitleProperty();

	/**
	 * Getter for the program state text property.
	 *
	 * @return the program state text property.
	 */
	StringProperty getStateTextProperty();

	/**
	 * Registers a subscriber to changes on the semester data. The boolean
	 * indicates whether a full reload is necessary or not.
	 *
	 * @param subscriber the subscriber that should be registered.
	 */
	void subscribeSemesterChanges(Flow.Subscriber<? super Boolean> subscriber);

	/**
	 * Registers a subscriber to changes on the timetable data. The boolean
	 * indicates whether a full reload is necessary or not.
	 *
	 * @param subscriber the subscriber that should be registered.
	 */
	void subscribeTimetablesChanges(
				Flow.Subscriber<? super Boolean> subscriber);

	/**
	 * Gracefully closes the model, should be called if the program is exited.
	 */
	void close();
}
