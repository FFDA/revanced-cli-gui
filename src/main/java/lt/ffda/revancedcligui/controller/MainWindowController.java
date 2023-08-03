package lt.ffda.revancedcligui.controller;

import javafx.fxml.FXML;
import javafx.scene.control.TabPane;

public class MainWindowController {
    @FXML
    private TabPane main_window;
    @FXML
    private TabRevancedController tab_revancedController;

    public void initialize() {
        this.tab_revancedController.setMainWindowController(this);
    }

    public TabPane getTabPane() {
        return this.main_window;
    }
}
