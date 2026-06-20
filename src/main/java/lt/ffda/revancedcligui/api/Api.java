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
import resource.IResource;
import resource.RevancedResource;

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
public class Api implements IApiResources {

    // Patterns to search for specific data of the patch information
    public final static Pattern PATTERN_NAME = Pattern.compile("Name: ([\\w ]+) Description: ");
    public final static Pattern PATTERN_DESCRIPTION = Pattern.compile("Description: (null)|Description: (.+)\\bC|Description: ([\\w \"-.]+)");
    public final static Pattern PATTERN_PACKAGES = Pattern.compile("Package name: ([\\w.]+)");
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
     * Get all patches available with selected cli and patches
     * @param cli path to selected cli
     * @param patches path to selected patches
     * @return list of patches in PatchDto objects
     * @throws IOException exception when getPatches() fails to read command output
     */
    public ArrayList<PatchDto> getPatches(String cli, String patches) throws IOException {
        Runtime runtime = Runtime.getRuntime();
        Process process = runtime.exec(String.format(getListPatchesCommand(), cli, patches));
        BufferedReader stdInput = new BufferedReader(new InputStreamReader(process.getInputStream()));
        List<String> packages = new ArrayList<>(); // Stores all patches from cli command output. One package per String
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
            Matcher matcher = PATTERN_NAME.matcher(patch);
            if (matcher.find()) {
                patchDto = new PatchDto(matcher.group(1));
            }
            if (patchDto == null) {
                continue;
            }
            matcher = PATTERN_DESCRIPTION.matcher(patch);
            if (matcher.find()) {
                for (int i = 1; i <= matcher.groupCount(); i++) {
                    if (matcher.group(i) != null) {
                        patchDto.setDescription(matcher.group(i).trim());
                        break;
                    }
                }
            }
            matcher = PATTERN_PACKAGES.matcher(patch);
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
     * Creates two commands. First command is to patch selected apk using cli and other provided dependencies
     * and user chosen options. Second command is for installing to chosen device. Seconds command might be null if
     * user do not check "Install after patching" checkbox.
     * @param cli filename of selected cli
     * @param patches filename of selected patches
     * @param integrations filename of selected integrations
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
        commandPatch.add(getCliResource().getFolderName() + File.separatorChar + cli);
        commandPatch.add("patch");
        if (Preferences.getInstance().getBooleanPreferenceValue(Preference.USE_KEYSTORE_FILE)) {
            commandPatch.add("--keystore=yt-ks.keystore");
        }
        if (Preferences.getInstance().getBooleanPreferenceValue(Preference.CLEAN_TEMPORARY_FILES)) {
            commandPatch.add("-p");
        }
        commandPatch.add("-b");
        commandPatch.add(getPatchesResource().getFolderName() + File.separatorChar + patches);
        commandPatch.add("-m");
        commandPatch.add(getIntegrationsResource().getFolderName() + File.separatorChar + integrations);
        if (exclude) {
            commandPatch.addAll(tabExcludeController.getExcludedPatches());
        }
        if (include) {
            commandPatch.addAll(tabIncludeController.getIncludedPatches());
        }
        commandPatch.add("-o");
        commandPatch.add(outputPath);
        commandPatch.add(getApkToPatchResource().getFolderName() + File.separatorChar + patchApk);
        ArrayList<String> commandInstall = null;
        if (Preferences.getInstance().getBooleanPreferenceValue(Preference.INSTALL_AFTER_PATCH)) {
            commandInstall = new ArrayList<>();
            commandInstall.add("java");
            commandInstall.add("-jar");
            commandInstall.add(getCliResource().getFolderName() + File.separatorChar + cli);
            commandInstall.add("utility");
            commandInstall.add("install");
            commandInstall.add("-a");
            commandInstall.add(outputPath);
            commandInstall.add(device.split(" - ")[0]);
        }
        return new ArrayList<>(Arrays.asList(commandPatch, commandInstall));
    }

    /**
     * Command to print information about supported apk version
     * @param cli name of with selected cli to use
     * @param patches name of with selected patches to use
     * @param packageName apk package name, e.g. com.google.android.youtube
     * @return formated string to be used as command
     */
    public String getSupportedVersionCommand(String cli, String patches, String packageName) {
        Api api = ApiFactory.getInstance().getApi();
        StringBuilder command = new StringBuilder("java -jar ");
        command.append(api.getCliResource().getFolderName())
                .append(File.separatorChar)
                .append(cli)
                .append(" list-versions")
                .append(" -f ")
                .append(packageName)
                .append(" -u ")
                .append(api.getPatchesResource().getFolderName())
                .append(File.separatorChar)
                .append(patches);
        return command.toString();
    }

    @Override
    public IResource getApkToPatchResource() {
        return RevancedResource.APK_TO_PATCH;
    }

    @Override
    public IResource getCliResource() {
        return RevancedResource.REVANCED_CLI;
    }

    @Override
    public IResource getPatchesResource() {
        return RevancedResource.REVANCED_PATCHES;
    }

    @Override
    public IResource getIntegrationsResource() {
        return RevancedResource.REVANCED_INTEGRATIONS;
    }

    @Override
    public IResource getMicroGResource() {
        return RevancedResource.MICROG;
    }

    @Override
    public IResource getPatchedApksResource() {
        return RevancedResource.PATCHED_APKS;
    }
}
