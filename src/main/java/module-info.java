module wcttt.gui {
	requires javafx.fxml;
	requires javafx.controls;
	requires de.nicolasgross.wcttt.lib;
	requires java.xml.bind;

	exports de.nicolasgross.wcttt.gui;

	opens de.nicolasgross.wcttt.gui.controller to javafx.fxml;
}