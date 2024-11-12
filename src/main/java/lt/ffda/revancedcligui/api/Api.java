package lt.ffda.revancedcligui.api;

import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import lt.ffda.revancedcligui.controller.TabExcludeController;
import lt.ffda.revancedcligui.controller.TabIncludeController;
import lt.ffda.revancedcligui.dto.PatchDto;
import lt.ffda.revancedcligui.util.ApiVersion;
import lt.ffda.revancedcligui.util.Preference;
import lt.ffda.revancedcligui.util.Preferences;
import lt.ffda.revancedcligui.util.Resource;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Api class that provides all the methods necessary to use revanced-cli-gui with selected API level in settings using
 * Api v4. For other Api versions overwrite necessary methods.
 */
public class Api {

    public Api() {}

    /**
     * Returns current API version
     * @return currently set API version
     */
    public ApiVersion getApiVersion() {
        return ApiVersion.V4;
    }

    /**
     * Command to print all the patches to stout. Most likely string still needs to be formatted to insert dependencies.
     * @return
     */
    public String getListPatchesCommand() {
        return "java -jar %1$s list-patches -p %2$s";
    }

    /**
     * Get all patches available with selected revancedCli and revancedPatches
     * @param revancedCli path to selected revanced-cli
     * @param revancedPatches path to selected revanced-patches
     * @return list of patches in PatchDto objects
     * @throws IOException exception when getPatches() fails to read command output
     */
    public ArrayList<PatchDto> getPatches(String revancedCli, String revancedPatches) throws IOException {
        Runtime runtime = Runtime.getRuntime();
        Process process = runtime.exec(String.format(getListPatchesCommand(), revancedCli, revancedPatches));
        BufferedReader stdInput = new BufferedReader(new InputStreamReader(process.getInputStream()));
        // Patterns to search for specific data of the patch information
        Pattern patternName = Pattern.compile("Name: ([\\w ]+) Description: ");
        Pattern patternDescription = Pattern.compile("Description: (null)|Description: (.+)\\bC|Description: ([\\w \"-.]+)");
        Pattern patternPackages = Pattern.compile("Package name: ([\\w.]+)");
        List<String> packages = new ArrayList<>(); // Stores all patches from revanced-cli command output. One package per String
        // Collects patch information
        StringBuilder singlePackage = new StringBuilder();
        String line;
        while ((line = stdInput.readLine()) != null) {
            if (line.isEmpty()) {
                packages.add(singlePackage.toString());
                singlePackage.setLength(0);
            } else {
                singlePackage.append(line);
                singlePackage.append(" ");
            }
        }
        stdInput.close();
        // Extracts information from the patch description and stores it in PatchDto object
        ArrayList<PatchDto> tempPatches = new ArrayList<>(); // Temp patches list
        for (String patch : packages) {
            PatchDto patchDto = null;
            Matcher matcher = patternName.matcher(patch);
            if (matcher.find()) {
                patchDto = new PatchDto(matcher.group(1));
            }
            if (patchDto == null) {
                continue;
            }
            matcher = patternDescription.matcher(patch);
            if (matcher.find()) {
                for (int i = 1; i <= matcher.groupCount(); i++) {
                    if (matcher.group(i) != null) {
                        patchDto.setDescription(matcher.group(i).trim());
                        break;
                    }
                }
            }
            matcher = patternPackages.matcher(patch);
            while (matcher.find()) {
                patchDto.getPackageName().add(matcher.group(1));
            }
            tempPatches.add(patchDto);
        }
        return tempPatches;
    }

    /**
     * Collects all selected patches to be excluded during patching process. Prepares it to be used in the command.
     * @param content VBox with all the patches listed
     * @return part of the command for excluding patches
     */
    public ArrayList<String> getExcludedPatches(VBox content) {
        ArrayList<String> excludePatches = new ArrayList<>();
        for (Node node : content.getChildren()) {
            HBox hBox = (HBox) node;
            CheckBox checkBox = (CheckBox) hBox.getChildren().get(0);
            if (checkBox.isSelected()) {
                excludePatches.add("-e");
                excludePatches.add(checkBox.getText());
            }
        }
        return excludePatches;
    }

    /**
     * Collects all selected patches to be included during patching process. Prepares it to be used in the command.
     * @param content VBox with all the patches listed
     * @return part of the command for including patches
     */
    public ArrayList<String> getIncludedPatches(VBox content) {
        ArrayList<String> includePatches = new ArrayList<>();
        for (Node node : content.getChildren()) {
            HBox hBox = (HBox) node;
            CheckBox checkBox = (CheckBox) hBox.getChildren().get(0);
            if (checkBox.isSelected()) {
                includePatches.add("-i");
                includePatches.add(checkBox.getText());
            }
        }
        return includePatches;
    }

    /**
     * Creates two commands. First command is to patch selected apk using Revance-Cli and other provided dependencies
     * and user chosen options.Second command is for installing to chosen device. Seconds command might be null if
     * user do not check "Install after patching" checkbox.
     * @param cli filename of selected Revanced-Cli
     * @param patches filename of selected Revanced-Patches
     * @param integrations filename of selected Revanced-integrations
     * @param patchApk filename of selected apk to be patched
     * @param exclude true if Exclude tab is visible, false - otherwise
     * @param include true if Include tab is visible, false - otherwise
     * @param outputPath path where patched file has to be placed
     * @param device full content of selected item in divices combobox
     * @param tabExcludeController instance of Exclude tab controller
     * @param tabIncludeController instance of Include tab controller
     * @return commands to execute for patching
     */
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
            commandPatch.add("-p");
        }
        commandPatch.add("-b");
        commandPatch.add(Resource.REVANCED_PATCHES.getFolderName() + File.separatorChar + patches);
        commandPatch.add("-m");
        commandPatch.add(Resource.REVANCED_INTEGRATIONS.getFolderName() + File.separatorChar + integrations);
        if (exclude) {
            commandPatch.addAll(tabExcludeController.getExcludedPatches());
        }
        if (include) {
            commandPatch.addAll(tabIncludeController.getIncludedPatches());
        }
        commandPatch.add("-o");
        commandPatch.add(outputPath);
        commandPatch.add(Resource.APK_TO_PATCH.getFolderName() + File.separatorChar + patchApk);
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
