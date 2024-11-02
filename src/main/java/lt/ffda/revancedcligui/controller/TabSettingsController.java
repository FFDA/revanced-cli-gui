package lt.ffda.revancedcligui.controller;

import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import lt.ffda.revancedcligui.util.Adb;
import lt.ffda.revancedcligui.util.Preference;
import lt.ffda.revancedcligui.util.Preferences;

public class TabSettingsController {
    @FXML
    private CheckBox download_dev_releases;
    @FXML
    private CheckBox use_embedded_adb;
    @FXML
    private CheckBox clean_temp_files;
    @FXML
    private CheckBox print_supported_versions;
    @FXML
    private CheckBox use_keystore_file;
    @FXML
    private CheckBox print_patch_command;

    public void initialize() {
        download_dev_releases.setSelected(Preferences.getInstance().getBooleanPreferenceValue(Preference.DOWNLOAD_DEV_RELEASES));
        use_embedded_adb.setSelected(Preferences.getInstance().getBooleanPreferenceValue(Preference.USE_EMBEDDED_ADB));
        clean_temp_files.setSelected(Preferences.getInstance().getBooleanPreferenceValue(Preference.CLEAN_TEMPORARY_FILES));
        print_supported_versions.setSelected(Preferences.getInstance().getBooleanPreferenceValue(Preference.PRINT_SUPPORTED_VERSIONS));
        use_keystore_file.setSelected(Preferences.getInstance().getBooleanPreferenceValue(Preference.USE_KEYSTORE_FILE));
        print_patch_command.setSelected(Preferences.getInstance().getBooleanPreferenceValue(Preference.PRINT_PATCH_COMMAND));
    }

    /**
     * Saves checkbox state to preferences.
     */
    public void onDownloadDevReleases() {
        Preferences.getInstance().setBooleanPreferenceValue(Preference.DOWNLOAD_DEV_RELEASES, download_dev_releases.isSelected());
    }

    /**
     * Saves checkbox state to preferences. Depending on the state creates ADB resources in root folder.
     */
    public void onUseEmbeddedAdb() {
        Preferences.getInstance().setBooleanPreferenceValue(Preference.USE_EMBEDDED_ADB, use_embedded_adb.isSelected());
        if (use_embedded_adb.isSelected()) {
            Adb.getInstance().saveAdb();
        } else {
            Adb.getInstance().killAdbServer();
        }
        Adb.getInstance().initAdb();
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
}
