package lt.ffda.patchercligui.controller;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import lt.ffda.patchercligui.api.ApiFactory;
import lt.ffda.patchercligui.dto.PatchDto;
import lt.ffda.patchercligui.tasks.ListPatches;
import lt.ffda.patchercligui.tasks.UpdatePatchListInclude;
import lt.ffda.patchercligui.util.PatchFilterListener;

import java.util.ArrayList;

public class TabIncludeController {
    @FXML
    private VBox content;
    @FXML
    private ComboBox<String> packages;
    @FXML
    private TextField filter;
    private final ArrayList<PatchDto> patchesList = new ArrayList<>();

    public void initialize() {
        filter.textProperty().addListener(new PatchFilterListener(content));
    }

    /**
     * Executes the task that load all available patches.
     * Available patches list retrieved from cli.
     * @param cli cli file to retrieve available patch list
     * @param patches patches apk to read the patch list from
     */
    public void loadPatches(String cli, String patches) {
        new Thread(new ListPatches(cli, patches, content, packages, patchesList)).start();
    }

    /**
     * Creates a string will all user selected patches to exclude
     * @return list with all patches to exclude
     */
    public ArrayList<String> getIncludedPatches() {
        return ApiFactory.getInstance().getApi().getIncludedPatches(content);
    }

    public void onPackageSelected() {
        new Thread(new UpdatePatchListInclude(content, patchesList, packages.getSelectionModel().getSelectedItem())).start();
    }
}
