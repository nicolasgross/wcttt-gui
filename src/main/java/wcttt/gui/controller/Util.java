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

import wcttt.gui.WctttGuiFatalException;
import wcttt.gui.model.Model;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Optional;

/**
 * Bundles functionality that is used in many GUI classes, e.g., information
 * alerts and error alerts.
 */
public class Util {

	static void exceptionAlert(Throwable ex) {
		Alert alert = new Alert(Alert.AlertType.ERROR);
		alert.setTitle("Error");
		alert.setHeaderText("An unexpected error has occurred");
		alert.setContentText(ex.getMessage());
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		ex.printStackTrace(pw);
		String exceptionText = sw.toString();
		Label label = new Label("The exception stacktrace was:");
		TextArea textArea = new TextArea(exceptionText);
		textArea.setEditable(false);
		textArea.setWrapText(true);
		textArea.setMaxWidth(Double.MAX_VALUE);
		textArea.setMaxHeight(Double.MAX_VALUE);
		GridPane.setVgrow(textArea, Priority.ALWAYS);
		GridPane.setHgrow(textArea, Priority.ALWAYS);
		GridPane expContent = new GridPane();
		expContent.setMaxWidth(Double.MAX_VALUE);
		expContent.add(label, 0, 0);
		expContent.add(textArea, 0, 1);
		alert.getDialogPane().setExpandableContent(expContent);
		alert.showAndWait();
	}

	static boolean confirmationAlert(String header, String question) {
		Alert alert = new Alert(Alert.AlertType.WARNING);
		alert.setTitle("Warning");
		alert.setHeaderText(header);
		alert.setContentText(question);
		alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);

		alert.getButtonTypes().setAll(ButtonType.YES, ButtonType.CANCEL);

		Optional<ButtonType> result = alert.showAndWait();
		return result.map(button -> button == ButtonType.YES).orElse(false);
	}

	static void errorAlert(String header, String message) {
		Alert alert = new Alert(Alert.AlertType.ERROR);
		alert.setTitle("Error");
		alert.setHeaderText(header);
		alert.setContentText(message);
		alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
		alert.showAndWait();
	}

	static void informationAlert(String header, String message) {
		Alert alert = new Alert(Alert.AlertType.INFORMATION);
		alert.setTitle("Information");
		alert.setHeaderText(header);
		alert.setContentText(message);
		alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
		alert.showAndWait();
	}

	static Optional<File> chooseFileToOpenDialog(Window owner) {
		FileChooser fileChooser = new FileChooser();
		fileChooser.getExtensionFilters().addAll(
				new FileChooser.ExtensionFilter("XML files", "*.xml"),
				new FileChooser.ExtensionFilter("All files", "*"));
		return Optional.ofNullable(fileChooser.showOpenDialog(owner));
	}

	static Optional<File> chooseFileToSaveDialog(Window owner) {
		FileChooser fileChooser = new FileChooser();
		fileChooser.getExtensionFilters().addAll(
				new FileChooser.ExtensionFilter("XML files", "*.xml"),
				new FileChooser.ExtensionFilter("All files", "*"));
		return Optional.ofNullable(fileChooser.showSaveDialog(owner));
	}

	static Optional<String> textInputDialog(String initial, String title,
	                                        String header, String contentText) {
		TextInputDialog dialog = new TextInputDialog(initial);
		dialog.setTitle(title);
		dialog.setHeaderText(header);
		dialog.setContentText(contentText);
		return dialog.showAndWait();
	}

	static Stage loadFxml(String fxmlPath, Controller controller, Stage stage,
	                      Model model, MainController mainController) {
		FXMLLoader loader = new FXMLLoader(Util.class.getResource(fxmlPath));
		if (controller != null) {
			loader.setController(controller);
		}
		try {
			Parent root = loader.load();
			Scene scene = new Scene(root);
			Stage actualStage;
			if (stage == null) {
				actualStage = new Stage();
				actualStage.initModality(Modality.APPLICATION_MODAL);
			} else {
				actualStage = stage;
			}
			actualStage.setScene(scene);
			Controller assignedController = loader.getController();
			assignedController.setup(actualStage,
				mainController.getHostServices(), mainController, model);
			return actualStage;
		} catch (IOException e) {
			throw new WctttGuiFatalException("Could not load '" + fxmlPath +
					"'", e);
		}
	}

	static void showStage(Stage stage, String title, int minWidth,
	                      int minHeight) {
		stage.titleProperty().bind(
				new SimpleStringProperty(title));
		stage.setMinWidth(minWidth);
		stage.setMinHeight(minHeight);
		stage.show();
		stage.sizeToScene();
		stage.requestFocus();
	}
}
