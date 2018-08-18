package de.nicolasgross.wcttt.gui.controller;

import de.nicolasgross.wcttt.gui.model.Model;
import de.nicolasgross.wcttt.lib.model.Timetable;
import de.nicolasgross.wcttt.lib.model.WctttModelException;
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

		timetableSelectionTable.getSortOrder().add(penaltyColumn);

		timetableSelectionTable.getSelectionModel().selectedItemProperty().
				addListener((observable, oldValue, newValue) -> {
			getMainController().getTimetableTableController().
					setTimetable(newValue);
		});

		timetableSelectionTable.getSelectionModel().setSelectionMode(
				SelectionMode.MULTIPLE);

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

		timetableSelectionTable.getSelectionModel().getSelectedIndices().
				addListener((ListChangeListener<Integer>) c -> {
					if (c.getList().size() > 1) {
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
	public void setup(Stage stage, Model model, MainController mainController) {
		super.setup(stage, model, mainController);
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
			}
			timetableSelectionTable.refresh();
			timetableSelectionTable.getSortOrder().add(penaltyColumn);
		});
	}
}
