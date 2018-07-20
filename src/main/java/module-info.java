module wcttt.gui {
	requires javafx.fxml;
	requires javafx.controls;
	requires de.nicolasgross.wcttt.lib;

	exports de.nicolasgross.wcttt.gui;

	opens de.nicolasgross.wcttt.gui.controller to javafx.fxml;
}