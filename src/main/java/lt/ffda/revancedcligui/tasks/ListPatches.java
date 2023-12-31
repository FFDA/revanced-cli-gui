package lt.ffda.revancedcligui.tasks;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import lt.ffda.revancedcligui.dto.PatchDto;
import lt.ffda.revancedcligui.util.CreateUiElement;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Uses revacned-cli command to list all available patches and adds them to UI for user to choose
 */
public class ListPatches extends Task<Void> {
    private final String command;
    private final ComboBox<String> packagesCombobox;
    private final VBox content;
    private final ArrayList<PatchDto> patches;

    /**
     * Creates a list of all available patches and adds them to provided VBox
     * @param command command to get all available patches
     * @param content UI element that shows patches to user
     * @param packagesCombobox combobox that lists all packages (apps) that patches are available for
     * @param patches list of PatchesDto object that collected patches will be stored at
     */
    public ListPatches(String command, VBox content, ComboBox<String> packagesCombobox, ArrayList<PatchDto> patches) {
        this.command = command;
        this.content = content;
        this.packagesCombobox = packagesCombobox;
        this.patches = patches;
    }

    @Override
    protected Void call() throws Exception {
        this.content.getChildren().clear();
        Runtime runtime = Runtime.getRuntime();
        Process process = runtime.exec(this.command);
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
        // Adds all patches to UI
        ArrayList<HBox> patchesUiList = new ArrayList<>(); // This that will be added to VBox content do be displayed for user
        ArrayList<String> patchPackages = new ArrayList<>(); // Stores unique package names
        tempPatches.forEach(p -> {
            patchesUiList.add(CreateUiElement.patchLine(p.getName(), p.getDescription()));
            p.getPackageName().forEach(m -> {if (!patchPackages.contains(m)) patchPackages.add(m);});
        });
        Collections.sort(patchPackages);
        Platform.runLater(() -> {
            this.content.getChildren().addAll(patchesUiList);
            this.packagesCombobox.getItems().setAll(patchPackages);
            this.patches.addAll(tempPatches);
        });
        return null;
    }
}
