package lt.ffda.revancedcligui.tasks;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import lt.ffda.revancedcligui.api.ApiFactory;
import lt.ffda.revancedcligui.dto.PatchDto;
import lt.ffda.revancedcligui.util.CreateUiElement;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Uses revanced-cli command to list all available patches and adds them to UI for user to choose
 */
public class ListPatches extends Task<Void> {
    private final String revancedCli;
    private final String revancedPatches;
    private final ComboBox<String> packagesCombobox;
    private final VBox content;
    private final ArrayList<PatchDto> patches;

    /**
     * Creates a list of all available patches and adds them to provided VBox
     * @param revancedCli relative path to ReVanced-CLI
     * @param revancedPatches relative path to ReVanced-patches
     * @param content UI element that shows patches to user
     * @param packagesCombobox combobox that lists all packages (apps) that patches are available for
     * @param patches list of PatchesDto object that collected patches will be stored at
     */
    public ListPatches(String revancedCli, String revancedPatches, VBox content, ComboBox<String> packagesCombobox, ArrayList<PatchDto> patches) {
        this.revancedCli = revancedCli;
        this.revancedPatches = revancedPatches;
        this.content = content;
        this.packagesCombobox = packagesCombobox;
        this.patches = patches;
    }

    @Override
    protected Void call() throws Exception {
        content.getChildren().clear();
        ArrayList<PatchDto> tempPatches = ApiFactory.getInstance().getApi().getPatches(revancedCli, revancedPatches);
        // Adds all patches to UI
        ArrayList<HBox> patchesUiList = new ArrayList<>(); // This that will be added to VBox content do be displayed for user
        ArrayList<String> patchPackages = new ArrayList<>(); // Stores unique package names
        tempPatches.forEach(p -> {
            patchesUiList.add(CreateUiElement.patchLine(p.getName(), p.getDescription()));
            p.getPackageName().forEach(m -> {if (!patchPackages.contains(m)) patchPackages.add(m);});
        });
        Collections.sort(patchPackages);
        Platform.runLater(() -> {
            content.getChildren().addAll(patchesUiList);
            packagesCombobox.getItems().setAll(patchPackages);
            patches.addAll(tempPatches);
        });
        return null;
    }
}
