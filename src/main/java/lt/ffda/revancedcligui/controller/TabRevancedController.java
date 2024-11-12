package lt.ffda.revancedcligui.controller;

import javafx.beans.value.ChangeListener;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import lt.ffda.revancedcligui.api.ApiFactory;
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
    private ComboBox<String> combobox_apk_to_patch;
    @FXML
    private ComboBox<String> combobox_revanced_cli;
    @FXML
    private ComboBox<String> combobox_revanced_patches;
    @FXML
    private ComboBox<String> combobox_revanced_integration;
    @FXML
    private Button on_revanced_integrations_refresh;
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
        new Thread(new DeviceCheck(combobox_devices, text_area)).start();
        revancedPatchesChangeListener = (observableValue, oldValue, newValue) -> {
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
        combobox_revanced_patches.getSelectionModel().selectedItemProperty().addListener(revancedPatchesChangeListener);
        if (ApiFactory.getInstance().getApi().getApiVersion() != ApiVersion.V4) {
            enableIntegrationsUi(false);
        }
        populateComboboxes();
        if (Preferences.getInstance().getBooleanPreferenceValue(Preference.PRINT_SUPPORTED_VERSIONS)) {
            printSupportedVersions();
        }
        checkbox_install.setSelected(Preferences.getInstance().getBooleanPreferenceValue(Preference.INSTALL_AFTER_PATCH));
        setTextAreaContextMenu();
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
                Preferences.getInstance().getBooleanPreferenceValue(Preference.DOWNLOAD_DEV_RELEASES),
                null
                ));
        executorService.submit(new ResourceCheck(
                Resource.REVANCED_INTEGRATIONS,
                this.text_area,
                combobox_revanced_integration,
                Preferences.getInstance().getBooleanPreferenceValue(Preference.DOWNLOAD_DEV_RELEASES),
                null
        ));
        executorService.submit(new ResourceCheck(
                Resource.MICROG,
                this.text_area,
                combobox_microg,
                Preferences.getInstance().getBooleanPreferenceValue(Preference.DOWNLOAD_DEV_RELEASES),
                null
        ));
        executorService.submit(new ResourceCheck(
                Resource.REVANCED_PATCHES,
                this.text_area,
                combobox_revanced_patches,
                Preferences.getInstance().getBooleanPreferenceValue(Preference.DOWNLOAD_DEV_RELEASES),
                revancedPatchesChangeListener,
                () -> {
                    if (Preferences.getInstance().getBooleanPreferenceValue(Preference.PRINT_SUPPORTED_VERSIONS)) {
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
        onApkToPatchRefresh();
        onRevancedCliRefresh();
        onRevancedPatchesRefresh();
        if (ApiFactory.getInstance().getApi().getApiVersion() == ApiVersion.V4) {
            onRevancedIntegrationsRefresh();
        }
        onMicroGRefresh();
    }

    /**
     * Patches and installs selected apk
     */
    public void patchAndInstall() {
        if (areResourceSelected()) {
            ExecutorService executorService = Executors.newSingleThreadExecutor();
            List<ArrayList<String>> commands = getCommands();
            if (Preferences.getInstance().getBooleanPreferenceValue(Preference.PRINT_PATCH_COMMAND)) {
                printPatchCommand(commands.get(0));
            }
            for (ArrayList<String> command: commands) {
                if (command != null) {
                    executorService.submit(new Patcher(command, text_area));
                }
            }
            executorService.shutdown();
        } else {
            text_area.appendText("Not all resources selected\n");
        }
    }

    /**
     * Install MicroG
     */
    public void installMicroG() {
        if (areMicroGResourcesSelected()) {
            new Thread(new Patcher(getMicroGCommand(), text_area)).start();
        } else {
            text_area.appendText("Not all resources selected\n");
        }
    }

    /**
     * Creates a command with user selected resources for patching and installing apk
     * @return command with user selected resources
     */
    private List<ArrayList<String>> getCommands() {
        return ApiFactory.getInstance().getApi().getCommands(combobox_revanced_cli.getValue(), combobox_revanced_patches.getValue(),
                combobox_revanced_integration.getValue(), combobox_apk_to_patch.getValue(), checkbox_exclude.isSelected(),
                checkbox_include.isSelected(), getOutputPath(), combobox_devices.getValue(), tabExcludeController,
                tabIncludeController);
    }

    /**
     * Creates a command to install MicroG with user selected MicroG version
     * @return command to install MicroG
     */
    private ArrayList<String> getMicroGCommand() {
        ArrayList<String> installMicroGCommand = new ArrayList<>();
        installMicroGCommand.add(Adb.getInstance().getAdb());
        installMicroGCommand.add("-s");
        installMicroGCommand.add(combobox_devices.getValue().split(" - ")[0]);
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
        if (this.combobox_apk_to_patch.getValue()== null || this.combobox_apk_to_patch.getValue().isEmpty()) {
            this.text_area.appendText("Please select an apk to patch\n");
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
        if (Preferences.getInstance().getBooleanPreferenceValue(Preference.INSTALL_AFTER_PATCH) && (this.combobox_devices.getValue()== null || this.combobox_devices.getValue().isEmpty())) {
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
     * Refresh list of apk files available for patching
     */
    public void onApkToPatchRefresh() {
        combobox_apk_to_patch.getItems().setAll(
                Arrays.stream(new File(Resource.APK_TO_PATCH.getFolderName()).list())
                        .sorted(Comparator.reverseOrder())
                        .collect(Collectors.toList())
        );
        combobox_apk_to_patch.getSelectionModel().select(0);
    }

    /**
     * Refresh ReVanced CLI jar file list
     */
    public void onRevancedCliRefresh() {
        combobox_revanced_cli.getItems().setAll(
                Arrays.stream(new File(Resource.REVANCED_CLI.getFolderName()).list())
                        .sorted(this.vc)
                        .collect(Collectors.toList())
        );
        combobox_revanced_cli.getSelectionModel().select(0);
    }

    /**
     * Refresh ReVanced Patches file list
     */
    public void onRevancedPatchesRefresh() {
        combobox_revanced_patches.getSelectionModel().selectedItemProperty().removeListener(this.revancedPatchesChangeListener);
        combobox_revanced_patches.getItems().setAll(
                Arrays.stream(new File(Resource.REVANCED_PATCHES.getFolderName()).list())
                        .sorted(this.vc)
                        .collect(Collectors.toList())
        );
        combobox_revanced_patches.getSelectionModel().selectedItemProperty().addListener(this.revancedPatchesChangeListener);
        combobox_revanced_patches.getSelectionModel().select(0);
    }

    /**
     * Refresh ReVanced Integration apk file list
     */
    public void onRevancedIntegrationsRefresh() {
        combobox_revanced_integration.getItems().setAll(
                Arrays.stream(new File(Resource.REVANCED_INTEGRATIONS.getFolderName()).list())
                        .sorted((this.vc))
                        .collect(Collectors.toList())
        );
        combobox_revanced_integration.getSelectionModel().select(0);
    }

    /**
     * Refresh microG apk file list
     */
    public void onMicroGRefresh() {
        combobox_microg.getItems().setAll(
                Arrays.stream(new File(Resource.MICROG.getFolderName()).list())
                        .sorted(Comparator.reverseOrder())
                        .collect(Collectors.toList())
        );
        combobox_microg.getSelectionModel().select(0);
    }

    /**
     * Refresh devices list
     */
    public void onDevicesRefresh() {
        new Thread(new DeviceCheck(combobox_devices, text_area)).start();
    }

    /**
     * Action on checking/unchecking of "Exclude" checkbox
     */
    public void onExclude() {
        if (checkbox_exclude.isSelected()) {
            try {
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/lt/ffda/revancedcligui/view/tab-exclude.fxml"));
                AnchorPane anchorPane = fxmlLoader.load();
                Tab tab = new Tab("Exclude");
                tab.setContent(anchorPane);
                mainWindowController.getTabPane().getTabs().add(mainWindowController.getTabPane().getTabs().size() - 1, tab);
                tabExcludeController = fxmlLoader.getController();
                loadPatchesExclude();
            } catch (IOException e) {
                this.text_area.appendText(e.toString());
            }
        } else {
            tabExcludeController = null;
            Tab tab = mainWindowController.getTabPane().getTabs().stream()
                    .filter(t -> t.getText().equals("Exclude"))
                    .findFirst()
                    .get();
            mainWindowController.getTabPane().getTabs().remove(tab);
        }
    }

    /**
     * Action on checking/unchecking of "Include" checkbox
     */
    public void onInclude() {
        if (checkbox_include.isSelected()) {
            try {
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/lt/ffda/revancedcligui/view/tab-include.fxml"));
                AnchorPane anchorPane = fxmlLoader.load();
                Tab tab = new Tab("Include");
                tab.setContent(anchorPane);
                mainWindowController.getTabPane().getTabs().add(mainWindowController.getTabPane().getTabs().size() - 1, tab);
                tabIncludeController = fxmlLoader.getController();
                loadPatchesInclude();
            } catch (IOException e) {
                this.text_area.appendText(e.toString());
            }
        } else {
            tabIncludeController = null;
            Tab tab = mainWindowController.getTabPane().getTabs().stream()
                    .filter(t -> t.getText().equals("Include"))
                    .findFirst()
                    .get();
            mainWindowController.getTabPane().getTabs().remove(tab);
        }
    }

    /**
     * Reload patches in Include/Exclude tabs if they are visible
     */
    public void reloadIncludeExcludePatches() {
        if (tabExcludeController != null) {
            loadPatchesExclude();
        }
        if (tabIncludeController != null) {
            loadPatchesInclude();
        }
    }

    /**
     * Load all available patches to the HBox of "Exclude" tab
     */
    private void loadPatchesExclude() {
        tabExcludeController.loadPatches(
                Resource.REVANCED_CLI.getFolderName() + File.separatorChar + combobox_revanced_cli.getValue(),
                Resource.REVANCED_PATCHES.getFolderName() + File.separatorChar + combobox_revanced_patches.getValue()
        );
    }

    /**
     * Load all available patches to the HBox of "Include" tab
     */
    private void loadPatchesInclude() {
        tabIncludeController.loadPatches(
                Resource.REVANCED_CLI.getFolderName() + File.separatorChar + combobox_revanced_cli.getValue(),
                Resource.REVANCED_PATCHES.getFolderName() + File.separatorChar + combobox_revanced_patches.getValue()
        );
    }

    /**
     * Prints supported YouTube version to the TextArea
     */
    private void printSupportedVersions() {
        new Thread(new ListVersions(text_area, combobox_revanced_cli.getValue(), combobox_revanced_patches.getValue())).start();
    }

    /**
     * Action on checking/unchecking of "Install" checkbox
     */
    public void onInstall() {
        Preferences.getInstance().setBooleanPreferenceValue(Preference.INSTALL_AFTER_PATCH, this.checkbox_install.isSelected());
    }

    /**
     * Creates path with filename where the patch will be saved. Output file name will be the same as input file with
     * "revanced_" prepended to it.
     * the file name.
     * @return path where patched file will be saved
     */
    private String getOutputPath() {
        String patchedApkFilename = String.format("revanced_%1$s", combobox_apk_to_patch.getValue());
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
        text_area.setContextMenu(contextMenu);
    }

    /**
     * Prints command to textArea that will be used to patch the apk file
     */
    private void printPatchCommand(ArrayList<String> command) {
        StringBuilder commandText = new StringBuilder("Patching command: ");
        commandText.append(String.join(" ", command)).append('\n');
        text_area.appendText(commandText.toString());
    }

    /**
     * Enable or disable UI elements associated with ReVanced integrations
     * @param status true - enable, false - disable
     */
    public void enableIntegrationsUi(boolean status) {
        combobox_revanced_integration.setDisable(!status);
        on_revanced_integrations_refresh.setDisable(!status);
    }

    /**
     * Clears text area from all output
     */
    public void onClearTextArea() {
        text_area.clear();
    }
}