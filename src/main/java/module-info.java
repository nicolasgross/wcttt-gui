module wcttt.gui {
	requires javafx.graphics;
	requires javafx.fxml;

	exports de.nicolasgross.wcttt.gui.controller to javafx.fxml;
	exports de.nicolasgross.wcttt.gui;
}