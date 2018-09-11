module wcttt.gui {
	requires javafx.fxml;
	requires javafx.controls;
	requires wcttt.lib;

	exports wcttt.gui;

	opens wcttt.gui.controller to javafx.fxml;
}
