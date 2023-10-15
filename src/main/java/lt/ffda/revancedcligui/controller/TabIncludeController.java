package lt.ffda.revancedcligui.controller;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import lt.ffda.revancedcligui.dto.PatchDto;
import lt.ffda.revancedcligui.tasks.ListPatches;
import lt.ffda.revancedcligui.tasks.UpdatePatchListInclude;

import java.util.ArrayList;

public class TabIncludeController {
    @FXML
    private VBox content;
    @FXML
    private ComboBox<String> packages;
    private final ArrayList<PatchDto> patches = new ArrayList<>();

    /**
     * Executes the task that load all available patches.
     * Available patches list retrieved from ReVanced-CLI using -l flag.
     * @param revancedCli ReVanced-CLI to retrieve available patch list
     * @param revancedPatches revanced-patched.apk to read the patch list from
     */
    public void loadPatches(String revancedCli, String revancedPatches) {
        new Thread(new ListPatches(String.format("java -jar %1$s list-patches -p %2$s",
                revancedCli,
                revancedPatches
        ), this.content, this.packages, this.patches)).start();
    }

    /**
     * Creates a string will all user selected patches to exclude
     * @return string with all patches to exclude
     */
    public ArrayList<String> getIncludedPatches() {
        ArrayList<String> includePatches = new ArrayList<>();
        for (Node node : this.content.getChildren()) {
            HBox hBox = (HBox) node;
            CheckBox checkBox = (CheckBox) hBox.getChildren().get(0);
            if (checkBox.isSelected()) {
                includePatches.add("-i");
                includePatches.add(checkBox.getText());
            }
        }
        return includePatches;
    }

    public void onPackageSelected() {
        new Thread(new UpdatePatchListInclude(this.content, this.patches, this.packages.getSelectionModel().getSelectedItem())).start();
    }
}
