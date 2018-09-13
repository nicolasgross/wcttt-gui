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
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import wcttt.gui.model.Model;
import wcttt.lib.model.Chair;
import wcttt.lib.model.Teacher;
import wcttt.lib.model.WctttModelException;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

/**
 * Controller for the view that is concerned with editing the chairs.
 */
public class EditChairsController extends SubscriberController<Boolean> {

	@FXML
	private EditChairTeachersController editTeachersController;

	@FXML
	private BorderPane rootPane;
	@FXML
	private VBox editChairVBox;
	@FXML
	private TreeView<TreeViewItemWrapper<?>> chairsTreeView;
	@FXML
	private Button addChairButton;
	@FXML
	private TextField nameField;
	@FXML
	private TextField abbreviationField;
	@FXML
	private Button addTeacherButton;
	@FXML
	private Button applyButton;

	@FXML
	protected void initialize() {
		// edit teacher vbox is stored on the right
		rootPane.setRight(null);

		chairsTreeView.setRoot(new TreeItem<>(
				new TreeViewItemWrapper<>("root")));
		chairsTreeView.setShowRoot(false);

		chairsTreeView.getSelectionModel().selectedItemProperty().addListener(
				(observable, oldValue, newValue) -> {
					if (newValue == null) {
						updateChairEditVBox(null);
						rootPane.setCenter(editChairVBox);
						rootPane.getCenter().disableProperty().setValue(true);
					} else if (newValue.getValue().getItem() instanceof Chair) {
						updateChairEditVBox((Chair) newValue.getValue().getItem());
						rootPane.setCenter(editChairVBox);
						rootPane.getCenter().disableProperty().setValue(false);
					} else {
						rootPane.setCenter(editTeachersController.
								getEditTeacherVBox((Teacher) newValue.
										getValue().getItem()));
						rootPane.getCenter().disableProperty().setValue(false);
					}
				});

		ContextMenu contextMenu = new ContextMenu();
		MenuItem deleteMenuItem = new MenuItem("Delete");
		deleteMenuItem.setOnAction(event -> contextDeleteAction());
		contextMenu.getItems().add(deleteMenuItem);

		chairsTreeView.setCellFactory(param -> {
			TreeCell<TreeViewItemWrapper<?>> cell = new TreeCell<>();

			cell.textProperty().bind(Bindings.when(cell.emptyProperty()).
					then("").otherwise(cell.itemProperty().asString()));

			cell.itemProperty().addListener((observable, oldValue, newValue) -> {
				if (newValue == null) {
					cell.setContextMenu(null);
				} else {
					cell.setContextMenu(contextMenu);
				}
			});

			return cell;
		});

		addChairButton.setOnAction(event -> {
			try {
				getModel().addChair(new Chair());
			} catch (WctttModelException e) {
				Util.errorAlert("Problem with editing the chairs",
						e.getMessage());
			}
		});

		addTeacherButton.setOnAction(event -> {
			Object selected = chairsTreeView.getSelectionModel().
					getSelectedItem().getValue().getItem();
			assert selected instanceof Chair;
			try {
				getModel().addTeacherToChair(new Teacher(), (Chair) selected);
			} catch (WctttModelException e) {
				Util.errorAlert("Problem with editing the chairs",
						e.getMessage());
			}
		});

		applyButton.setOnAction(event -> applyButtonAction());
	}

	private void contextDeleteAction() {
		TreeItem<TreeViewItemWrapper<?>> selection =
				chairsTreeView.getSelectionModel().getSelectedItem();
		boolean confirmed;
		if (selection.getValue().getItem() instanceof Chair) {
			confirmed = Util.confirmationAlert("Confirm deletion of " +
					"chair", "Are you sure you want to delete the " +
					"selected chair?");
		} else {
			confirmed = Util.confirmationAlert("Confirm deletion of " +
					"teacher", "Are you sure you want to delete the " +
					"selected teacher?");
		}
		if (confirmed) {
			try {
				if (selection.getValue().getItem() instanceof Chair) {
					getModel().removeChair(
							(Chair) selection.getValue().getItem());
				} else {
					getModel().removeTeacherFromChair(
							(Teacher) selection.getValue().getItem(),
							(Chair) selection.getParent().getValue().getItem());
				}
			} catch (WctttModelException e) {
				Util.errorAlert("Problem with editing the chairs",
						e.getMessage());
			}
		}
	}

	private void applyButtonAction() {
		TreeItem<TreeViewItemWrapper<?>> selection =
				chairsTreeView.getSelectionModel().getSelectedItem();
		assert selection.getValue().getItem() instanceof Chair;
		Chair chair = (Chair) selection.getValue().getItem();
		try {
			getModel().updateChairData(chair, nameField.getText(),
					abbreviationField.getText());
		} catch (WctttModelException e) {
			Util.errorAlert("Problem with editing the chair",
					e.getMessage());
		}
	}

	private void updateChairEditVBox(Chair chair) {
		if (chair == null) {
			nameField.setText("");
			abbreviationField.setText("");
		} else {
			nameField.setText(chair.getName());
			abbreviationField.setText(chair.getAbbreviation());
		}
	}

	private List<TreeItem<TreeViewItemWrapper<?>>> createChairTree() {
		List<TreeItem<TreeViewItemWrapper<?>>> chairItems = new LinkedList<>();
		for (Chair chair : getModel().getChairs()) {
			TreeViewItemWrapper<?> chairWrapper =
					new TreeViewItemWrapper<>(chair);
			TreeItem<TreeViewItemWrapper<?>> chairItem =
					new TreeItem<>(chairWrapper);

			for (Teacher teacher : chair.getTeachers()) {
				TreeViewItemWrapper<?> teacherWrapper =
						new TreeViewItemWrapper<>(teacher);
				TreeItem<TreeViewItemWrapper<?>> teacherItem =
						new TreeItem<>(teacherWrapper);
				chairItem.getChildren().add(teacherItem);
			}
			chairItems.add(chairItem);
		}

		// keep expanded state of tree items
		for (TreeItem<TreeViewItemWrapper<?>> oldItem :
				chairsTreeView.getRoot().getChildren()) {
			if (oldItem.isExpanded()) {
				for (TreeItem<TreeViewItemWrapper<?>> newItem : chairItems) {
					if (newItem.getValue().getItem().equals(
							oldItem.getValue().getItem())) {
						newItem.setExpanded(true);
					}
				}
			}
		}
		return chairItems;
	}

	private void updateCoursesTreeView(boolean fullReloadNecessary) {
		if (fullReloadNecessary) {
			List<TreeItem<TreeViewItemWrapper<?>>> chairs = createChairTree();
			Platform.runLater(() -> {
				chairsTreeView.getRoot().getChildren().clear();
				chairsTreeView.getRoot().getChildren().addAll(chairs);
				chairsTreeView.getRoot().getChildren().sort(
						Comparator.comparing(t -> t.getValue().toString()));
			});
		} else {
			Platform.runLater(() -> {
				chairsTreeView.refresh();
				chairsTreeView.getRoot().getChildren().sort(
						Comparator.comparing(t -> t.getValue().toString()));
			});
		}
	}

	@Override
	public void setup(Stage stage, HostServices hostServices,
	                  MainController mainController, Model model) {
		super.setup(stage, hostServices, mainController, model);
		getModel().subscribeSemesterChanges(this);
		editTeachersController.setup(stage, hostServices,
			mainController, model);
		updateCoursesTreeView(true);
	}

	@Override
	public void onNext(Boolean item) {
		updateCoursesTreeView(item);
		getSubscription().request(1);
	}
}
