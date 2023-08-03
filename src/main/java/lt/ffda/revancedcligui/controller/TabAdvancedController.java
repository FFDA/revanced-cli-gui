package lt.ffda.revancedcligui.controller;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import lt.ffda.revancedcligui.tasks.ListPatches;

public class TabAdvancedController {
    @FXML
    private VBox content;

    /**
     * Executes the task that load all available patches.
     * Available patches list retrieved from ReVanced-CLI using -l flag.
     * @param revancedCli ReVanced-CLI to retrieve available patch list
     * @param youtubeApk YouTube apk to retrieve available patch list. (Comments in revanved-cli shows that it most likely will be removed in the future)
     * @param revancedPatches revanced-patched.apk to read the patch list from
     */
    public void loadPatches(String revancedCli, String youtubeApk, String revancedPatches) {
        new Thread(new ListPatches(String.format("java -jar %1$s -a %2$s -b %3$s -l",
                revancedCli,
                youtubeApk,
                revancedPatches
        ), this.content)).start();
    }

    /**
     * Creates a string will all user selected patches to exclude
     * @return string with all patches to exclude
     */
    public String getExcludedPatches() {
        StringBuilder stringBuilder = new StringBuilder();
        for (Node node : this.content.getChildren()) {
            HBox hBox = (HBox) node;
            CheckBox checkBox = (CheckBox) hBox.getChildren().get(0);
            if (checkBox.isSelected()) {
                stringBuilder.append(String.format(" -e %1$s", checkBox.getText()));
            }
        }
        return stringBuilder.toString();
    }
}
