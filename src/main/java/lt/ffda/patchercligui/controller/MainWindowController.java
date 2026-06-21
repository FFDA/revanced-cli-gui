package lt.ffda.patchercligui.controller;

import javafx.fxml.FXML;
import javafx.scene.control.TabPane;

public class MainWindowController {
    @FXML
    private TabPane main_window;
    @FXML
    private TabPatcherController tab_patcherController;
    @FXML
    private TabSettingsController tab_settingsController;

    public void initialize() {
        tab_patcherController.setMainWindowController(this);
        tab_settingsController.setTabPatcherController(tab_patcherController);
    }

    public TabPane getTabPane() {
        return main_window;
    }
}
