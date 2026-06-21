package lt.ffda.patchercligui.controller;

import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import lt.ffda.patchercligui.api.ApiFactory;
import lt.ffda.patchercligui.util.ApiVersion;
import lt.ffda.patchercligui.util.Preference;
import lt.ffda.patchercligui.util.Preferences;

public class TabSettingsController {
    @FXML
    private CheckBox download_dev_releases;
    @FXML
    private CheckBox clean_temp_files;
    @FXML
    private CheckBox print_supported_versions;
    @FXML
    private CheckBox use_keystore_file;
    @FXML
    private CheckBox print_patch_command;
    @FXML
    private ComboBox<ApiVersion> api_version;
    private TabPatcherController tabPatcherController;

    public void initialize() {
        Preferences prefs = Preferences.getInstance();
        download_dev_releases.setSelected(prefs.getBooleanPreferenceValue(Preference.DOWNLOAD_DEV_RELEASES));
        clean_temp_files.setSelected(prefs.getBooleanPreferenceValue(Preference.CLEAN_TEMPORARY_FILES));
        print_supported_versions.setSelected(prefs.getBooleanPreferenceValue(Preference.PRINT_SUPPORTED_VERSIONS));
        use_keystore_file.setSelected(prefs.getBooleanPreferenceValue(Preference.USE_KEYSTORE_FILE));
        print_patch_command.setSelected(prefs.getBooleanPreferenceValue(Preference.PRINT_PATCH_COMMAND));
        String api = prefs.getStringPreferenceValue(Preference.API_VERSION);
        if (api == null) {
            api = ApiVersion.DEFAULT_API.name();
            prefs.setStringPreferenceValue(Preference.API_VERSION, api);
        }
        api_version.setValue(ApiVersion.valueOf(api));
        api_version.getItems().setAll(ApiVersion.values());
    }

    /**
     * Saves checkbox state to preferences.
     */
    public void onDownloadDevReleases() {
        Preferences.getInstance().setBooleanPreferenceValue(Preference.DOWNLOAD_DEV_RELEASES, download_dev_releases.isSelected());
    }

    /**
     * Saves checkbox state to preferences.
     */
    public void onCleanTempFiles() {
        Preferences.getInstance().setBooleanPreferenceValue(Preference.CLEAN_TEMPORARY_FILES, clean_temp_files.isSelected());
    }

    /**
     * Saves checkbox state to preferences.
     */
    public void onPrintSupportedVersions() {
        Preferences.getInstance().setBooleanPreferenceValue(Preference.PRINT_SUPPORTED_VERSIONS, print_supported_versions.isSelected());
    }

    /**
     * Saves checkbox state to preferences.
     */
    public void onUseKeystoreFile() {
        Preferences.getInstance().setBooleanPreferenceValue(Preference.USE_KEYSTORE_FILE, use_keystore_file.isSelected());
    }

    /**
     * Saves checkbox state to preferences.
     */
    public void onPrintPatchCommand() {
        Preferences.getInstance().setBooleanPreferenceValue(Preference.PRINT_PATCH_COMMAND, print_patch_command.isSelected());
    }

    /**
     * Saves selected Api level to preferences. Reloads contents of Exclude/Include tabs.
     */
    public void onApiVersionChange() {
        Preferences.getInstance().setStringPreferenceValue(Preference.API_VERSION, api_version.getValue().name());
        ApiFactory.getInstance().changeApi(api_version.getValue());
        tabPatcherController.enableIntegrationsUi(api_version.getValue() == ApiVersion.V4);
        tabPatcherController.reloadIncludeExcludePatches();
    }

    /**
     * Set reference of the controller for "Patcher" tab
     * @param tabPatcherController controller of the "Patcher" tab
     */
    public void setTabPatcherController(TabPatcherController tabPatcherController) {
        this.tabPatcherController = tabPatcherController;
    }
}
