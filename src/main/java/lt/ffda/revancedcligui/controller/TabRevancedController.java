package lt.ffda.revancedcligui.controller;

import javafx.beans.value.ChangeListener;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import lt.ffda.revancedcligui.tasks.*;
import javafx.fxml.FXML;
import lt.ffda.revancedcligui.util.*;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
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
    private ComboBox<String> combobox_microg;
    @FXML
    private ComboBox<String> combobox_devices;
    @FXML
    private TextArea text_area;
    @FXML
    private CheckBox checkbox_exclude;
    @FXML
    private CheckBox checkbox_include;
    @FXML
    private CheckBox checkbox_install;
    // Needed as a variable that it could be removed at appropriate time when refreshing the list
    private ChangeListener<String> revancedPatchesChangeListener;
    private final VersionComparator vc = new VersionComparator();

    public void initialize() {
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
        if (Preferences.getInstance().getPreferenceValue(Preference.PRINT_SUPPORTED_VERSIONS)) {
            this.printSupportedVersions();
        }
        this.checkbox_install.setSelected(Preferences.getInstance().getPreferenceValue(Preference.INSTALL_AFTER_PATCH));
        this.setTextAreaContextMenu();
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
                Preferences.getInstance().getPreferenceValue(Preference.DOWNLOAD_DEV_RELEASES),
                null
                ));
        executorService.submit(new ResourceCheck(
                Resource.REVANCED_INTEGRATIONS,
                this.text_area,
                combobox_revanced_integration,
                Preferences.getInstance().getPreferenceValue(Preference.DOWNLOAD_DEV_RELEASES),
                null
        ));
        executorService.submit(new ResourceCheck(
                Resource.MICROG,
                this.text_area,
                combobox_microg,
                Preferences.getInstance().getPreferenceValue(Preference.DOWNLOAD_DEV_RELEASES),
                null
        ));
        executorService.submit(new ResourceCheck(
                Resource.REVANCED_PATCHES,
                this.text_area,
                combobox_revanced_patches,
                Preferences.getInstance().getPreferenceValue(Preference.DOWNLOAD_DEV_RELEASES),
                revancedPatchesChangeListener,
                () -> {
                    if (Preferences.getInstance().getPreferenceValue(Preference.PRINT_SUPPORTED_VERSIONS)) {
                        printSupportedVersions();
                    }
                }
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
        this.onMicroGRefresh();
    }

    /**
     * Patches and installs YouTube
     */
    public void patchAndInstall() {
        if (this.areResourceSelected()) {
            ExecutorService executorService = Executors.newSingleThreadExecutor();
            List<ArrayList<String>> commands = getCommands();
            if (Preferences.getInstance().getPreferenceValue(Preference.PRINT_PATCH_COMMAND)) {
                this.printPatchCommand(commands.get(0));
            }
            for (ArrayList<String> command: commands) {
                if (command != null) {
                    executorService.submit(new Patcher(command, this.text_area));
                }
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
     *
     * @return command with user selected resources
     */
    private List<ArrayList<String>> getCommands() {
        ArrayList<String> commandPatch = new ArrayList<>();
        commandPatch.add("java");
        commandPatch.add("-jar");
        commandPatch.add(Resource.REVANCED_CLI.getFolderName() + File.separatorChar + this.combobox_revanced_cli.getValue());
        commandPatch.add("patch");
        if (Preferences.getInstance().getPreferenceValue(Preference.USE_KEYSTORE_FILE)) {
            commandPatch.add("--keystore=yt-ks.keystore");
        }
        if (Preferences.getInstance().getPreferenceValue(Preference.CLEAN_TEMPORARY_FILES)) {
            commandPatch.add("-p");
        }
        commandPatch.add("-b");
        commandPatch.add(Resource.REVANCED_PATCHES.getFolderName() + File.separatorChar + this.combobox_revanced_patches.getValue());
        commandPatch.add("-m");
        commandPatch.add(Resource.REVANCED_INTEGRATIONS.getFolderName() + File.separatorChar + this.combobox_revanced_integration.getValue());
        if (this.checkbox_exclude.isSelected()) {
            commandPatch.addAll(this.tabExcludeController.getExcludedPatches());
        }
        if (this.checkbox_include.isSelected()) {
            commandPatch.addAll(this.tabIncludeController.getIncludedPatches());
        }
        commandPatch.add("-o");
        String outputPath = getOutputPath();
        commandPatch.add(outputPath);
        commandPatch.add(Resource.YOUTUBE_APK.getFolderName() + File.separatorChar + this.combobox_youtube_apk.getValue());
        ArrayList<String> commandInstall = null;
        if (Preferences.getInstance().getPreferenceValue(Preference.INSTALL_AFTER_PATCH)) {
            commandInstall = new ArrayList<>();
            commandInstall.add("java");
            commandInstall.add("-jar");
            commandInstall.add(Resource.REVANCED_CLI.getFolderName() + File.separatorChar + this.combobox_revanced_cli.getValue());
            commandInstall.add("utility");
            commandInstall.add("install");
            commandInstall.add("-a");
            commandInstall.add(outputPath);
            commandInstall.add(this.combobox_devices.getValue().split(" - ")[0]);
        }
        return new ArrayList<>(Arrays.asList(commandPatch, commandInstall));
    }

    /**
     * Creates a command to install MicroG with user selected MicroG version
     * @return command to install MicroG
     */
    private ArrayList<String> getMicroGCommand() {
        ArrayList<String> installMicroGCommand = new ArrayList<>();
        installMicroGCommand.add(Adb.getInstance().getAdb());
        installMicroGCommand.add("-s");
        installMicroGCommand.add(this.combobox_devices.getValue().split(" - ")[0]);
        installMicroGCommand.add("install");
        installMicroGCommand.add(Resource.MICROG.getFolderName() + File.separatorChar + this.combobox_microg.getValue());
        return installMicroGCommand;
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
        if (Preferences.getInstance().getPreferenceValue(Preference.INSTALL_AFTER_PATCH) && (this.combobox_devices.getValue()== null || this.combobox_devices.getValue().isEmpty())) {
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
        if (this.combobox_microg.getValue() == null || this.combobox_microg.getValue().isEmpty()) {
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
                        .sorted(this.vc)
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
                        .sorted(this.vc)
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
                        .sorted((this.vc))
                        .collect(Collectors.toList())
        );
        this.combobox_revanced_integration.getSelectionModel().select(0);
    }

    /**
     * Refresh microG apk file list
     */
    public void onMicroGRefresh() {
        this.combobox_microg.getItems().setAll(
                Arrays.stream(new File(Resource.MICROG.getFolderName()).list())
                        .sorted(Comparator.reverseOrder())
                        .collect(Collectors.toList())
        );
        this.combobox_microg.getSelectionModel().select(0);
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

    /**
     * Prints supported YouTube version to the TextArea
     */
    private void printSupportedVersions() {
        new Thread(new ListVersions(this.text_area, this.combobox_revanced_cli.getValue(), this.combobox_revanced_patches.getValue())).start();
    }

    /**
     * Action on checking/unchecking of "Install" checkbox
     */
    public void onInstall() {
        Preferences.getInstance().setPreferenceValue(Preference.INSTALL_AFTER_PATCH, this.checkbox_install.isSelected());
    }

    /**
     * Creates path with filename where the patch will be saved. If file selected for patching has word "youtube" in it,
     * it will be changed to "revanced_youtube. If it does not contain word "youtube" "revanced_" will be prepended to
     * the file name.
     * @return path where patched file will be saved
     */
    private String getOutputPath() {
        String patchedApkFilename = this.combobox_youtube_apk.getValue().contains("youtube") ? this.combobox_youtube_apk.getValue().replace("youtube", "revanced_youtube") : String.format("revanced_%1$s", this.combobox_youtube_apk.getValue());
        StringBuilder outputPath = new StringBuilder();
        outputPath.append(Resource.PATCHED_APKS.getFolderName()).append(File.separatorChar).append(patchedApkFilename);
        return outputPath.toString();
    }

    /**
     * Creates custom contextMenu for text area where output of all processes are printed out. Only has a button to
     * clear the text area.
     */
    private void setTextAreaContextMenu() {
        ContextMenu contextMenu = new ContextMenu();
        MenuItem menuItemClear = new MenuItem("Clear");
        menuItemClear.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                text_area.clear();
            }
        });
        contextMenu.getItems().add(menuItemClear);
        this.text_area.setContextMenu(contextMenu);
    }

    /**
     * Prints command to textArea that will be used to patch the apk file
     */
    private void printPatchCommand(ArrayList<String> command) {
        StringBuilder commandText = new StringBuilder("Patching command: ");
        commandText.append(String.join(" ", command)).append('\n');
        this.text_area.appendText(commandText.toString());
    }
}