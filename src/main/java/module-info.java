module wcttt.gui {
	requires javafx.fxml;
	requires javafx.controls;

	exports de.nicolasgross.wcttt.gui;

	opens de.nicolasgross.wcttt.gui.controller to javafx.fxml;
}