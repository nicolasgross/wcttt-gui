/*
 * WCT³ (WIAI Course Timetabling Tool) is a software that strives to automate
 * the timetabling process at the WIAI faculty of the University of Bamberg.
 *
 * WCT³ GUI comprises functionality to view generated timetables, edit semester
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

package de.nicolasgross.wcttt.gui.model;

import de.nicolasgross.wcttt.lib.model.Period;
import de.nicolasgross.wcttt.lib.model.Semester;
import de.nicolasgross.wcttt.lib.model.Teacher;
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

	List<Period> getPeriods();

	StringProperty getTitleProperty();

	StringProperty getStateTextProperty();

	void subscribeSemesterChanges(Flow.Subscriber<? super Boolean> subscriber);

	void subscribeTimetablesChanges(
				Flow.Subscriber<? super Boolean> subscriber);

	void close();
}
