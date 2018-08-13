package de.nicolasgross.wcttt.gui;

import de.nicolasgross.wcttt.gui.controller.MainController;
import de.nicolasgross.wcttt.gui.model.Model;
import de.nicolasgross.wcttt.gui.model.ModelImpl;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

	public static void main(String[] args) {
		launch(args);
	}

	/**
	 * The main entry point for all JavaFX applications.
	 * The start method is called after the init method has returned,
	 * and after the system is ready for the application to begin running.
	 *
	 * <p>
	 * NOTE: This method is called on the JavaFX Application Thread.
	 * </p>
	 *
	 * @param primaryStage the primary stage for this application, onto which
	 *                     the application scene can be set. The primary stage
	 *                     will be embedded in the browser if the application
	 *                     was launched as an applet. Applications may create
	 *                     other stages, if needed, but they will not be primary
	 *                     stages and will not be embedded in the browser.
	 * @throws Exception if something goes wrong
	 */
	@Override
	public void start(Stage primaryStage) throws Exception {
		FXMLLoader loader = new FXMLLoader(getClass().getResource(
				"/fxml/main.fxml"));
		Parent root = loader.load();
		Scene scene = new Scene(root);
		primaryStage.setScene(scene);

		MainController controller = loader.getController();
		Model model = new ModelImpl();
		controller.setup(primaryStage, model, controller);

		primaryStage.titleProperty().bind(model.getTitleProperty());
		primaryStage.setMinWidth(800);
		primaryStage.setMinHeight(500);
		primaryStage.show();
		root.requestFocus();
	}

	// TODO check conflicting pre assignments before generating timetable

}