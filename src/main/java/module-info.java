module de.nicolasgross.wcttt.gui {
	requires javafx.fxml;
	requires javafx.controls;
	requires de.nicolasgross.wcttt.lib;
	requires de.nicolasgross.wcttt.core;

	exports de.nicolasgross.wcttt.gui;

	opens de.nicolasgross.wcttt.gui.controller to javafx.fxml;
}