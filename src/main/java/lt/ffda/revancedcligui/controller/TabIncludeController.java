package lt.ffda.revancedcligui.controller;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import lt.ffda.revancedcligui.api.ApiFactory;
import lt.ffda.revancedcligui.dto.PatchDto;
import lt.ffda.revancedcligui.tasks.ListPatches;
import lt.ffda.revancedcligui.tasks.UpdatePatchListInclude;
import lt.ffda.revancedcligui.util.PatchFilterListener;

import java.util.ArrayList;

public class TabIncludeController {
    @FXML
    private VBox content;
    @FXML
    private ComboBox<String> packages;
    @FXML
    private TextField filter;
    private final ArrayList<PatchDto> patches = new ArrayList<>();

    public void initialize() {
        filter.textProperty().addListener(new PatchFilterListener(content));
    }

    /**
     * Executes the task that load all available patches.
     * Available patches list retrieved from ReVanced-CLI using -l flag.
     * @param revancedCli ReVanced-CLI to retrieve available patch list
     * @param revancedPatches revanced-patched.apk to read the patch list from
     */
    public void loadPatches(String revancedCli, String revancedPatches) {
        new Thread(new ListPatches(revancedCli, revancedPatches, content, packages, patches)).start();
    }

    /**
     * Creates a string will all user selected patches to exclude
     * @return list with all patches to exclude
     */
    public ArrayList<String> getIncludedPatches() {
        return ApiFactory.getInstance().getApi().getIncludedPatches(content);
    }

    public void onPackageSelected() {
        new Thread(new UpdatePatchListInclude(content, patches, packages.getSelectionModel().getSelectedItem())).start();
    }
}
