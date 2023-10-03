package lt.ffda.revancedcligui.controller;

import javafx.beans.value.ChangeListener;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import lt.ffda.revancedcligui.tasks.DeviceCheck;
import lt.ffda.revancedcligui.tasks.Patcher;
import lt.ffda.revancedcligui.tasks.ResourceCheck;
import javafx.fxml.FXML;
import lt.ffda.revancedcligui.util.*;
import org.json.JSONArray;

import java.io.*;
import java.net.URL;
import java.util.Arrays;
import java.util.Comparator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class TabRevancedController {
    private MainWindowController mainWindowController;
    private TabExcludeController tabExcludeController;
    private TabIncludeController tabIncludeController;
    @FXML
    private ComboBox<String> combobox_youtube_apk;
    @FXML
    private ComboBox<String> combobox_revanced_cli;
    @FXML
    private ComboBox<String> combobox_revanced_patches;
    @FXML
    private ComboBox<String> combobox_revanced_integration;
    @FXML
    private ComboBox<String> combobox_vanced_microg;
    @FXML
    private ComboBox<String> combobox_devices;
    @FXML
    private TextArea text_area;
    @FXML
    private CheckBox checkbox_exclude;
    @FXML
    private CheckBox checkbox_include;
    // Needed as a variable that it could be removed at appropriate time when refreshing the list
    private ChangeListener<String> revancedPatchesChangeListener;

    public void initialize() {
        this.getSupportedYoutubeVersion();
        new Thread(new DeviceCheck(this.combobox_devices, this.text_area)).start();
        this.revancedPatchesChangeListener = (observableValue, oldValue, newValue) -> {
            if (checkbox_exclude.isSelected()) {
                if (newValue != null && !newValue.equals(oldValue)) {
                    loadPatchesExclude();
                }
            }
            if (checkbox_include.isSelected()) {
                if (newValue != null && !newValue.equals(oldValue)) {
                    loadPatchesInclude();
                }
            }
        };
        this.combobox_revanced_patches.getSelectionModel().selectedItemProperty().addListener(this.revancedPatchesChangeListener);
        this.populateComboboxes();
    }

    /**
     * Set reference of MainWindowController
     * @param mainWindowController MainWindowController object
     */
    public void setMainWindowController(MainWindowController mainWindowController) {
        this.mainWindowController = mainWindowController;
    }

    /**
     * Initiates tasks to update revanced-cli, revanced-integrations, vanced microg and revanced-pathces resources
     */
    public void updateResources() {
        ExecutorService executorService = Executors.newFixedThreadPool(2);
        executorService.submit(new ResourceCheck(
                Resource.REVANCED_CLI,
                this.text_area,
                combobox_revanced_cli,
                Preferences.getInstance().getPreferenceValue(Preference.DOWNLOAD_DEV_RELEASES)
                ));
        executorService.submit(new ResourceCheck(
                Resource.REVANCED_INTEGRATIONS,
                this.text_area,
                combobox_revanced_integration,
                Preferences.getInstance().getPreferenceValue(Preference.DOWNLOAD_DEV_RELEASES)
        ));
        executorService.submit(new ResourceCheck(
                Resource.VANCED_MICROG,
                this.text_area,
                combobox_vanced_microg,
                Preferences.getInstance().getPreferenceValue(Preference.DOWNLOAD_DEV_RELEASES)
        ));
        executorService.submit(new ResourceCheck(
                Resource.REVANCED_PATCHES,
                this.text_area,
                combobox_revanced_patches,
                Preferences.getInstance().getPreferenceValue(Preference.DOWNLOAD_DEV_RELEASES),
                revancedPatchesChangeListener
        ));
        executorService.shutdown();
    }

    /**
     * Displays all resources in appropriate comboboxes. Sorts the - newest at the top.
     */
    private void populateComboboxes() {
        this.onYoutubeApkRefresh();
        this.onRevancedCliRefresh();
        this.onRevancedPatchesRefresh();
        this.onRevancedIntegrationsRefresh();
        this.onVancedMicroGRefresh();
    }

    /**
     * Retrieves from ReVanced GitHub and displays in TextArea all supported YouTube versions
     */
    private void getSupportedYoutubeVersion() {
        try (InputStream inputStream = new URL("https://github.com/ReVanced/revanced-patches/raw/main/patches.json").openStream()) {
            String jsonString = new String(inputStream.readAllBytes());
            JSONArray versionArray = new JSONArray(jsonString).getJSONObject(0).getJSONArray("compatiblePackages").getJSONObject(0).getJSONArray("versions");
            this.text_area.appendText(String.format("Supported youtube version by the latest patches: %1$s\n", versionArray.toString()));
        } catch (IOException e) {
            this.text_area.appendText("Error occurred while list supported youtube version. No internet connection?\n");
        }
    }

    /**
     * Patches and installs YouTube
     */
    public void patchAndInstall() {
        if (this.areResourceSelected()) {
            ExecutorService executorService = Executors.newSingleThreadExecutor();
            for (String command: getCommands()) {
                executorService.submit(new Patcher(command, this.text_area));
            }
            executorService.shutdown();
        } else {
            this.text_area.appendText("Not all resources selected\n");
        }
    }

    /**
     * Install MicroG
     */
    public void installMicroG() {
        if (this.areMicroGResourcesSelected()) {
            new Thread(new Patcher(getMicroGCommand(), this.text_area)).start();
        } else {
            this.text_area.appendText("Not all resources selected\n");
        }
    }

    /**
     * Creates a command with user selected resources for patching and installing apk
     * @return command with user selected resources
     */
    private String[] getCommands() {
        String patchedApk = this.combobox_youtube_apk.getValue().contains("youtube") ? this.combobox_youtube_apk.getValue().replace("youtube", "revanced_youtube") : String.format("revanced_%1$s", this.combobox_youtube_apk.getValue());
        String commandPatch = String.format("java -jar %1$s patch %2$s -b %3$s -m %4$s%5$s%6$s -o %7$s %8$s",
                Resource.REVANCED_CLI.getFolderName() + File.separatorChar + this.combobox_revanced_cli.getValue(),
                Preferences.getInstance().getPreferenceValue(Preference.CLEAN_TEMPORARY_FILES) ? " -p" : "",
                Resource.REVANCED_PATCHES.getFolderName() + File.separatorChar + this.combobox_revanced_patches.getValue(),
                Resource.REVANCED_INTEGRATIONS.getFolderName() + File.separatorChar + this.combobox_revanced_integration.getValue(),
                this.checkbox_exclude.isSelected() ? this.tabExcludeController.getExcludedPatches() : "",
                this.checkbox_include.isSelected() ? this.tabIncludeController.getIncludedPatches() : "",
                patchedApk,
                Resource.YOUTUBE_APK.getFolderName() + File.separatorChar + this.combobox_youtube_apk.getValue()
        );
        String commandInstall = String.format("java -jar %1$s utility install -a %2$s %3$s",
                Resource.REVANCED_CLI.getFolderName() + File.separatorChar + this.combobox_revanced_cli.getValue(),
                patchedApk,
                this.combobox_devices.getValue().split(" - ")[0]);
        return new String[]{commandPatch, commandInstall};
    }

    /**
     * Creates a command to install MicroG with user selected MicroG version
     * @return command to install MicroG
     */
    private String getMicroGCommand() {
        return String.format("%1$s -s %2$s install %3$s",
                Adb.getInstance().getAdb(),
                this.combobox_devices.getValue().split(" - ")[0],
                Resource.VANCED_MICROG.getFolderName() + File.separatorChar + this.combobox_vanced_microg.getValue());
    }

    /**
     * Checks if all mandatory resources in comboboxes are selected
     * @return true - all mandatory resources are selected, false - otherwise
     */
    private boolean areResourceSelected() {
        boolean selected = true;
        if (this.combobox_youtube_apk.getValue()== null || this.combobox_youtube_apk.getValue().isEmpty()) {
            this.text_area.appendText("Please select an Youtube apk\n");
            selected = false;
        }
        if (this.combobox_revanced_cli.getValue()== null || this.combobox_revanced_cli.getValue().isEmpty()) {
            this.text_area.appendText("Please select a Revanced CLI version\n");
            selected = false;
        }
        if (this.combobox_revanced_patches.getValue()== null || this.combobox_revanced_patches.getValue().isEmpty()) {
            this.text_area.appendText("Please select a Revanced Patches version\n");
            selected = false;
        }
        if (this.combobox_devices.getValue()== null || this.combobox_devices.getValue().isEmpty()) {
            this.text_area.appendText("Please select a device\n");
            selected = false;
        }
        return selected;
    }

    /**
     * Checks if all mandatory resource in comboboxes are selected
     * @return true - all necessary resource are selected, false - otherwise
     */
    private boolean areMicroGResourcesSelected() {
        boolean selected = true;
        if (this.combobox_devices.getValue() == null || this.combobox_devices.getValue().isEmpty()) {
            this.text_area.appendText("Please select a device\n");
            selected = false;
        }
        if (this.combobox_vanced_microg.getValue() == null || this.combobox_vanced_microg.getValue().isEmpty()) {
            this.text_area.appendText("Please select a MicroG apk\n");
            selected = false;
        }
        return selected;
    }

    /**
     * Refresh list of YouTube apk files
     */
    public void onYoutubeApkRefresh() {
        this.combobox_youtube_apk.getItems().setAll(
                Arrays.stream(new File(Resource.YOUTUBE_APK.getFolderName()).list())
                        .sorted(Comparator.reverseOrder())
                        .collect(Collectors.toList())
        );
        this.combobox_youtube_apk.getSelectionModel().select(0);
    }

    /**
     * Refresh ReVanced CLI jar file list
     */
    public void onRevancedCliRefresh() {
        this.combobox_revanced_cli.getItems().setAll(
                Arrays.stream(new File(Resource.REVANCED_CLI.getFolderName()).list())
                        .sorted(Comparator.reverseOrder())
                        .collect(Collectors.toList())
        );
        this.combobox_revanced_cli.getSelectionModel().select(0);
    }

    /**
     * Refresh ReVanced Patches file list
     */
    public void onRevancedPatchesRefresh() {
        this.combobox_revanced_patches.getSelectionModel().selectedItemProperty().removeListener(this.revancedPatchesChangeListener);
        this.combobox_revanced_patches.getItems().setAll(
                Arrays.stream(new File(Resource.REVANCED_PATCHES.getFolderName()).list())
                        .sorted(Comparator.reverseOrder())
                        .collect(Collectors.toList())
        );
        this.combobox_revanced_patches.getSelectionModel().selectedItemProperty().addListener(this.revancedPatchesChangeListener);
        this.combobox_revanced_patches.getSelectionModel().select(0);
    }

    /**
     * Refresh ReVanced Integration apk file list
     */
    public void onRevancedIntegrationsRefresh() {
        this.combobox_revanced_integration.getItems().setAll(
                Arrays.stream(new File(Resource.REVANCED_INTEGRATIONS.getFolderName()).list())
                        .sorted(Comparator.reverseOrder())
                        .collect(Collectors.toList())
        );
        this.combobox_revanced_integration.getSelectionModel().select(0);
    }

    /**
     * Refresh ReVanced Patches apk file list
     */
    public void onVancedMicroGRefresh() {
        this.combobox_vanced_microg.getItems().setAll(
                Arrays.stream(new File(Resource.VANCED_MICROG.getFolderName()).list())
                        .sorted(Comparator.reverseOrder())
                        .collect(Collectors.toList())
        );
        this.combobox_vanced_microg.getSelectionModel().select(0);
    }

    /**
     * Refresh devices list
     */
    public void onDevicesRefresh() {
        new Thread(new DeviceCheck(this.combobox_devices, this.text_area)).start();
    }

    /**
     * Action on checking/unchecking of "Exclude" checkbox
     */
    public void onExclude() {
        if (this.checkbox_exclude.isSelected()) {
            try {
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/lt/ffda/revancedcligui/view/tab-exclude.fxml"));
                AnchorPane anchorPane = fxmlLoader.load();
                Tab tab = new Tab("Exclude");
                tab.setContent(anchorPane);
                this.mainWindowController.getTabPane().getTabs().add(this.mainWindowController.getTabPane().getTabs().size() - 1, tab);
                this.tabExcludeController = fxmlLoader.getController();
                this.loadPatchesExclude();
            } catch (IOException e) {
                this.text_area.appendText(e.toString());
            }
        } else {
            this.tabExcludeController = null;
            Tab tab = this.mainWindowController.getTabPane().getTabs().stream()
                    .filter(t -> t.getText().equals("Exclude"))
                    .findFirst()
                    .get();
            this.mainWindowController.getTabPane().getTabs().remove(tab);
        }
    }

    /**
     * Action on checking/unchecking of "Include" checkbox
     */
    public void onInclude() {
        if (this.checkbox_include.isSelected()) {
            try {
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/lt/ffda/revancedcligui/view/tab-include.fxml"));
                AnchorPane anchorPane = fxmlLoader.load();
                Tab tab = new Tab("Include");
                tab.setContent(anchorPane);
                this.mainWindowController.getTabPane().getTabs().add(this.mainWindowController.getTabPane().getTabs().size() - 1, tab);
                this.tabIncludeController = fxmlLoader.getController();
                this.loadPatchesInclude();
            } catch (IOException e) {
                this.text_area.appendText(e.toString());
            }
        } else {
            this.tabIncludeController = null;
            Tab tab = this.mainWindowController.getTabPane().getTabs().stream()
                    .filter(t -> t.getText().equals("Include"))
                    .findFirst()
                    .get();
            this.mainWindowController.getTabPane().getTabs().remove(tab);
        }
    }

    /**
     * Load all available patches to the HBox of "Exclude" tab
     */
    private void loadPatchesExclude() {
        this.tabExcludeController.loadPatches(
                Resource.REVANCED_CLI.getFolderName() + File.separatorChar + this.combobox_revanced_cli.getValue(),
                Resource.REVANCED_PATCHES.getFolderName() + File.separatorChar + this.combobox_revanced_patches.getValue()
        );
    }

    /**
     * Load all available patches to the HBox of "Include" tab
     */
    private void loadPatchesInclude() {
        this.tabIncludeController.loadPatches(
                Resource.REVANCED_CLI.getFolderName() + File.separatorChar + this.combobox_revanced_cli.getValue(),
                Resource.REVANCED_PATCHES.getFolderName() + File.separatorChar + this.combobox_revanced_patches.getValue()
        );
    }
}