package lt.ffda.revancedcligui.util;

import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

public class CreateUiElement {

    /**
     * Creates one line of the available patch list
     * @param name name of the patch
     * @param description description of the patch
     */
    public static HBox patchLine(String name, String description) {
        HBox hBox = new HBox();
        hBox.getStyleClass().add("patch-list-item");
        CheckBox checkBox = new CheckBox(name);
        Label label = new Label(String.format(" - %1$s", description));
        hBox.getChildren().addAll(checkBox, label);
        return hBox;
    }
}
