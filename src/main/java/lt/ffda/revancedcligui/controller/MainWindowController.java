package lt.ffda.revancedcligui.controller;

import javafx.fxml.FXML;
import javafx.scene.control.TabPane;

public class MainWindowController {
    @FXML
    private TabPane main_window;
    @FXML
    private TabRevancedController tab_revancedController;
    @FXML
    private TabSettingsController tab_settingsController;

    public void initialize() {
        tab_revancedController.setMainWindowController(this);
        tab_settingsController.setTabRevancedController(tab_revancedController);
    }

    public TabPane getTabPane() {
        return main_window;
    }
}
