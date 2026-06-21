package lt.ffda.patchercligui.tasks;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import lt.ffda.patchercligui.api.ApiFactory;
import lt.ffda.patchercligui.dto.PatchDto;
import lt.ffda.patchercligui.util.CreateUiElement;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Uses cli command to list all available patches and adds them to UI for user to choose
 */
public class ListPatches extends Task<Void> {
    private final String cli;
    private final String patches;
    private final ComboBox<String> packagesCombobox;
    private final VBox content;
    private final ArrayList<PatchDto> patchesList;

    /**
     * Creates a list of all available patches and adds them to provided VBox
     * @param cli relative path to cli
     * @param patches relative path to patches
     * @param content UI element that shows patches to user
     * @param packagesCombobox combobox that lists all packages (apps) that patches are available for
     * @param patchesList list of PatchesDto object that collected patches will be stored at
     */
    public ListPatches(String cli, String patches, VBox content, ComboBox<String> packagesCombobox, ArrayList<PatchDto> patchesList) {
        this.cli = cli;
        this.patches = patches;
        this.content = content;
        this.packagesCombobox = packagesCombobox;
        this.patchesList = patchesList;
    }

    @Override
    protected Void call() throws Exception {
        content.getChildren().clear();
        ArrayList<PatchDto> tempPatches = ApiFactory.getInstance().getApi().getPatches(cli, patches);
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
            patchesList.addAll(tempPatches);
        });
        return null;
    }
}
