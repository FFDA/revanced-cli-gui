package lt.ffda.revancedcligui.controller;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import lt.ffda.revancedcligui.api.ApiFactory;
import lt.ffda.revancedcligui.dto.PatchDto;
import lt.ffda.revancedcligui.tasks.ListPatches;
import lt.ffda.revancedcligui.tasks.UpdatePatchListExclude;
import lt.ffda.revancedcligui.util.PatchFilterListener;

import java.util.ArrayList;

public class TabExcludeController {
    @FXML
    private VBox content;
    @FXML
    private ComboBox<String> packages;
    @FXML
    private TextField filter;
    private ArrayList<PatchDto> patches = new ArrayList<>();

    public void initialize() {
        filter.textProperty().addListener(new PatchFilterListener(content));
    }

    /**
     * Executes the task that load all available patches.
     * Available patches list retrieved from ReVanced-CLI using -l flag.
     * @param revancedCli ReVanced-CLI to retrieve available patch list
     * @param revancedPatches revanced-patches.apk to read the patch list from
     */
    public void loadPatches(String revancedCli, String revancedPatches) {
        new Thread(new ListPatches(revancedCli, revancedPatches, content, packages, patches)).start();
    }

    /**
     * Creates a string will all user selected patches to exclude
     * @return string with all patches to exclude
     */
    public ArrayList<String> getExcludedPatches() {
        return ApiFactory.getInstance().getApi().getExcludedPatches(content);
    }

    /**
     * Updates patches list to show patches available only for selected package (app)
     */
    public void onPackageSelected() {
        new Thread(new UpdatePatchListExclude(content, patches, packages.getSelectionModel().getSelectedItem())).start();
    }
}
