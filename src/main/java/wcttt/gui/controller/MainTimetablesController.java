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

package wcttt.gui.controller;

import javafx.application.HostServices;
import wcttt.gui.model.Model;
import wcttt.lib.model.Timetable;
import wcttt.lib.model.WctttModelException;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

/**
 * Controller for the timetable selection in the side menu.
 */
public class MainTimetablesController extends SubscriberController<Boolean> {

	@FXML
	private TableView<Timetable> timetableSelectionTable;
	@FXML
	private TableColumn<Timetable, String> timetableColumn;
	@FXML
	private TableColumn<Timetable, Double> penaltyColumn;

	@FXML
	protected void initialize() {
		timetableSelectionTable.setPlaceholder(new Label("No timetables " +
				"generated for this semester"));

		timetableSelectionTable.getSelectionModel().setSelectionMode(
				SelectionMode.MULTIPLE);

		timetableSelectionTable.getSortOrder().add(penaltyColumn);

		timetableSelectionTable.getSelectionModel().selectedItemProperty().
				addListener((observable, oldValue, newValue) -> {
			getMainController().getTimetableTableController().
					setTimetable(newValue);
		});

		ContextMenu contextMenu = new ContextMenu();
		MenuItem renameMenuItem = new MenuItem("Rename");
		renameMenuItem.setOnAction(event -> {
			Timetable selection = timetableSelectionTable.getSelectionModel().
					getSelectedItem();
			Optional<String> name = Util.textInputDialog(selection.getName(),
					"Rename timetable", "Enter new timetable name", "Name:");
			name.ifPresent(s -> {
				try {
					getModel().updateTimetableName(selection, s);
				} catch (WctttModelException e) {
					Util.errorAlert("Timetable could not be renamed",
							e.getMessage());
				}
			});
		});
		contextMenu.getItems().add(renameMenuItem);

		MenuItem deleteMenuItem = new MenuItem("Delete");
		deleteMenuItem.setOnAction(event -> {
			List<Timetable> selection = timetableSelectionTable.
					getSelectionModel().getSelectedItems();
			boolean confirmed = Util.confirmationAlert("Confirm deletion of " +
					"timetables", "Are you sure you want to delete the " +
					"selected timetable" + (selection.size() == 1 ? "" : "s")
					+ "?");
			if (confirmed) {
				for (Timetable timetable : new LinkedList<>(selection)) {
					getModel().removeTimetable(timetable);
				}
			}
		});
		contextMenu.getItems().add(deleteMenuItem);

		timetableSelectionTable.getSelectionModel().getSelectedCells().
				addListener((ListChangeListener<TablePosition>) c -> {
					if (c.getList().size() != 1) {
						renameMenuItem.setDisable(true);
					} else {
						renameMenuItem.setDisable(false);
					}
				});

		timetableSelectionTable.setRowFactory(param -> {
			TableRow<Timetable> row = new TableRow<>();
			row.contextMenuProperty().bind(
					Bindings.when(row.emptyProperty())
							.then((ContextMenu) null)
							.otherwise(contextMenu)
			);
			return row;
		});

		timetableColumn.setReorderable(false);
		timetableColumn.setCellValueFactory(new PropertyValueFactory<>("name"));

		penaltyColumn.setReorderable(false);
		penaltyColumn.setCellValueFactory(new PropertyValueFactory<>(
						"softConstraintPenalty"));
	}

	@Override
	public void setup(Stage stage, HostServices hostServices,
	                  MainController mainController, Model model) {
		super.setup(stage, hostServices, mainController, model);
		model.subscribeTimetablesChanges(this);
		updateGui(true);
	}

	@Override
	public void onNext(Boolean item) {
		updateGui(item);
		getSubscription().request(1);
	}

	private void updateGui(boolean fullReloadNecessary) {
		Platform.runLater(() -> {
			if (fullReloadNecessary) {
				timetableSelectionTable.setItems(getModel().getTimetables());
			} else {
				timetableSelectionTable.refresh();
			}
			timetableSelectionTable.getSortOrder().add(penaltyColumn);
		});
	}
}
