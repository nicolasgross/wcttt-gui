package de.nicolasgross.wcttt.gui.controller;

import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.stage.FileChooser;
import javafx.stage.Window;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Optional;

public class Util {

	/**
	 * Fancy Alert for exceptions.
	 *
	 * @implNote taken from the internet
	 * @param ex
	 *            the exception, which information is shown in the alert.
	 */
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

	/**
	 * Opens a dialog for confirmation.
	 *
	 * @param header
	 *            for the confirmation dialog box.
	 * @param question
	 *            for the confirmation dialog box.
	 * @return boolean {@code true} if confirmed or {@code false} if it is
	 *         canceled.
	 *
	 */
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

	/**
	 * Returns an error alert.
	 * @param header header of the alert.
	 * @param message message of the alert.
	 * @return the alert.
	 */
	static void errorAlert(String header, String message) {
		Alert alert = new Alert(Alert.AlertType.ERROR);
		alert.setTitle("Error");
		alert.setHeaderText(header);
		alert.setContentText(message);
		alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
		alert.showAndWait();
	}

	/**
	 * Opens a FileChooser Dialog
	 *
	 * @param owner the parent window.
	 *
	 * @return Optional of a nullable.
	 */
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

	static Optional<String> textInputDialog(Window owner, String initial,
	                                        String title, String header,
	                                        String contentText) {
		TextInputDialog dialog = new TextInputDialog(initial);
		dialog.setTitle(title);
		dialog.setHeaderText(header);
		dialog.setContentText(contentText);
		return dialog.showAndWait();
	}
}
