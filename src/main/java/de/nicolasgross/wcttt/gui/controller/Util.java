package de.nicolasgross.wcttt.gui.controller;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
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
	public static void exceptionAlert(Throwable ex) {
		Alert alert = errorAlert("Fatal Error!", "An unexpected error has occurred", ex.getMessage());
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
	 * @param title
	 *            for the confirmation dialog box.
	 * @param header
	 *            for the confirmation dialog box.
	 * @param question
	 *            for the confirmation dialog box.
	 * @return boolean {@code true} if confirmed or {@code false} if it is
	 *         canceled.
	 *
	 */
	public static boolean confirmationAlert(String title, String header, String question) {
		Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
		alert.setTitle(title);
		alert.setHeaderText(header);
		alert.setContentText(question);
		alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);

		ButtonType buttonTypeOk = new ButtonType("Ok");
		ButtonType buttonTypeCancel = new ButtonType("Cancel");

		alert.getButtonTypes().setAll(buttonTypeOk, buttonTypeCancel);

		Optional<ButtonType> result = alert.showAndWait();
		return result.map(button -> button == buttonTypeOk).orElse(false);
	}

	/**
	 * Returns an error alert.
	 * @param title title of the alert.
	 * @param header header of the alert.
	 * @param message message of the alert.
	 * @return the alert.
	 */
	public static Alert errorAlert(String title, String header, String message) {
		Alert alert = new Alert(Alert.AlertType.ERROR);
		alert.setTitle(title);
		alert.setHeaderText(header);
		alert.setContentText(message);

		return alert;
	}

	/**
	 * Opens a FileChooser Dialog
	 *
	 * @param owner the parent window.
	 *
	 * @return Optional of a nullable.
	 */
	public static Optional<File> choosePathAlert(Window owner) {
		FileChooser fileChooser = new FileChooser();
		fileChooser.getExtensionFilters().addAll(
				new FileChooser.ExtensionFilter("XML Files", "*.xml"));
		return Optional.ofNullable(fileChooser.showOpenDialog(owner));
	}
}
