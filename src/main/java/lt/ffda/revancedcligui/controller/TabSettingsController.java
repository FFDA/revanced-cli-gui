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

    public void initialize() {
        this.download_dev_releases.setSelected(Preferences.getInstance().getPreferenceValue(Preference.DOWNLOAD_DEV_RELEASES));
        this.use_embedded_adb.setSelected(Preferences.getInstance().getPreferenceValue(Preference.USE_EMBEDDED_ADB));
        this.clean_temp_files.setSelected(Preferences.getInstance().getPreferenceValue(Preference.CLEAN_TEMPORARY_FILES));
        this.print_supported_versions.setSelected(Preferences.getInstance().getPreferenceValue(Preference.PRINT_SUPPORTED_VERSIONS));
    }

    /**
     * Saves checkbox state to preferences.
     */
    public void onDownloadDevReleases() {
        Preferences.getInstance().setPreferenceValue(Preference.DOWNLOAD_DEV_RELEASES, this.download_dev_releases.isSelected());
    }

    /**
     * Saves checkbox state to preferences. Depending on the state creates ADB resources in root folder.
     */
    public void onUseEmbeddedAdb() {
        Preferences.getInstance().setPreferenceValue(Preference.USE_EMBEDDED_ADB, this.use_embedded_adb.isSelected());
        if (this.use_embedded_adb.isSelected()) {
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
        Preferences.getInstance().setPreferenceValue(Preference.CLEAN_TEMPORARY_FILES, this.clean_temp_files.isSelected());
    }

    /**
     * Saves checkbox state to preferences.
     */
    public void onPrintSupportedVersions() {
        Preferences.getInstance().setPreferenceValue(Preference.PRINT_SUPPORTED_VERSIONS, this.print_supported_versions.isSelected());
    }
}
