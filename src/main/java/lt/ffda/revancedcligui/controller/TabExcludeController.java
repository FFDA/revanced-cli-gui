package lt.ffda.revancedcligui.controller;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
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
        this.filter.textProperty().addListener(new PatchFilterListener(this.content));
    }

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
    public ArrayList<String> getExcludedPatches() {
        ArrayList<String> excludePatches = new ArrayList<>();
        for (Node node : this.content.getChildren()) {
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
     * Updates patches list to show patches available only for selected package (app)
     */
    public void onPackageSelected() {
        new Thread(new UpdatePatchListExclude(this.content, this.patches, this.packages.getSelectionModel().getSelectedItem())).start();
    }
}
