package lt.ffda.revancedcligui.api;

import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import lt.ffda.revancedcligui.controller.TabExcludeController;
import lt.ffda.revancedcligui.controller.TabIncludeController;
import lt.ffda.revancedcligui.util.ApiVersion;
import lt.ffda.revancedcligui.util.Preference;
import lt.ffda.revancedcligui.util.Preferences;
import lt.ffda.revancedcligui.util.Resource;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ApiV5 extends Api {

    @Override
    public ApiVersion getApiVersion() {
        return ApiVersion.V5;
    }

    @Override
    public ArrayList<String> getExcludedPatches(VBox content) {
        ArrayList<String> excludePatches = new ArrayList<>();
        for (Node node : content.getChildren()) {
            HBox hBox = (HBox) node;
            CheckBox checkBox = (CheckBox) hBox.getChildren().get(0);
            if (checkBox.isSelected()) {
                excludePatches.add("-d");
                excludePatches.add(checkBox.getText());
            }
        }
        return excludePatches;
    }

    @Override
    public ArrayList<String> getIncludedPatches(VBox content) {
        ArrayList<String> includePatches = new ArrayList<>();
        for (Node node : content.getChildren()) {
            HBox hBox = (HBox) node;
            CheckBox checkBox = (CheckBox) hBox.getChildren().get(0);
            if (checkBox.isSelected()) {
                includePatches.add("-e");
                includePatches.add(checkBox.getText());
            }
        }
        return includePatches;
    }

    @Override
    public List<ArrayList<String>> getCommands(String cli, String patches, String integrations, String patchApk,
                                               boolean exclude, boolean include, String outputPath, String device,
                                               TabExcludeController tabExcludeController, TabIncludeController tabIncludeController) {
        ArrayList<String> commandPatch = new ArrayList<>();
        commandPatch.add("java");
        commandPatch.add("-jar");
        commandPatch.add(Resource.REVANCED_CLI.getFolderName() + File.separatorChar + cli);
        commandPatch.add("patch");
        if (Preferences.getInstance().getBooleanPreferenceValue(Preference.USE_KEYSTORE_FILE)) {
            commandPatch.add("--keystore=yt-ks.keystore");
        }
        if (Preferences.getInstance().getBooleanPreferenceValue(Preference.CLEAN_TEMPORARY_FILES)) {
            commandPatch.add("--purge");
        }
        commandPatch.add("-p");
        commandPatch.add(Resource.REVANCED_PATCHES.getFolderName() + File.separatorChar + patches);
        if (exclude) {
            commandPatch.addAll(tabExcludeController.getExcludedPatches());
        }
        if (include) {
            commandPatch.addAll(tabIncludeController.getIncludedPatches());
        }
        commandPatch.add("-o");
        commandPatch.add(outputPath);
        commandPatch.add(Resource.YOUTUBE_APK.getFolderName() + File.separatorChar + patchApk);
        ArrayList<String> commandInstall = null;
        if (Preferences.getInstance().getBooleanPreferenceValue(Preference.INSTALL_AFTER_PATCH)) {
            commandInstall = new ArrayList<>();
            commandInstall.add("java");
            commandInstall.add("-jar");
            commandInstall.add(Resource.REVANCED_CLI.getFolderName() + File.separatorChar + cli);
            commandInstall.add("utility");
            commandInstall.add("install");
            commandInstall.add("-a");
            commandInstall.add(outputPath);
            commandInstall.add(device.split(" - ")[0]);
        }
        return new ArrayList<>(Arrays.asList(commandPatch, commandInstall));
    }
}
