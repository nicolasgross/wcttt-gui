package de.nicolasgross.wcttt.gui.controller;

import de.nicolasgross.wcttt.gui.model.Model;
import de.nicolasgross.wcttt.lib.model.Timetable;
import de.nicolasgross.wcttt.lib.model.WctttModelException;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.util.List;
import java.util.Optional;

public class MainTimetablesController
		extends SubscriberController<List<Timetable>> {

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

		timetableSelectionTable.setRowFactory(param -> {
			TableRow<Timetable> row = new TableRow<>();
			ContextMenu contextMenu = new ContextMenu();

			MenuItem renameMenuItem = new MenuItem("Rename");
			renameMenuItem.setOnAction(event -> {
				Optional<String> name = Util.textInputDialog(
						getStage().getOwner(), row.getItem().getName(),
						"Rename timetable", "Enter new timetable name",
						"Name:");
				name.ifPresent(s -> {
					try {
						getModel().updateTimetableName(row.getItem(), s);
					} catch (WctttModelException e) {
						Util.errorAlert("Timetable name is already taken",
								e.getMessage());
					}
				});
			});
			contextMenu.getItems().add(renameMenuItem);

			MenuItem deleteMenuItem = new MenuItem("Delete");
			deleteMenuItem.setOnAction(event -> {
				boolean confirmed = Util.confirmationAlert("Confirm deletion " +
								"of a timetable", "Are you sure you want to " +
						"delete this timetable?");
				if (confirmed) {
					getModel().removeTimetable(row.getItem());
				}
			});
			contextMenu.getItems().add(deleteMenuItem);

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
		Platform.runLater(this::updateGui);
	}

	@Override
	public void onNext(List<Timetable> item) {
		Platform.runLater(this::updateGui);
		getSubscription().request(1);
	}

	private void updateGui() {
		timetableSelectionTable.setItems(getModel().getTimetables());
		timetableSelectionTable.refresh();
		timetableSelectionTable.getSortOrder().add(penaltyColumn);
	}
}
