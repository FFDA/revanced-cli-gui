package lt.ffda.revancedcligui.util;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class PatchFilterListener implements ChangeListener<String> {

    private VBox content;

    public PatchFilterListener(VBox content) {
        this.content = content;
    }

    @Override
    public void changed(ObservableValue<? extends String> observableValue, String s, String t1) {
        if (!t1.trim().isEmpty()) {
            for (Node node : content.getChildren()) {
                HBox hBox = (HBox) node;
                CheckBox checkBox = (CheckBox) hBox.getChildren().get(0);
                Label label = (Label) hBox.getChildren().get(1);
                if (!checkBox.getText().contains(t1) && !label.getText().contains(t1)) {
                    node.setVisible(false);
                    node.setManaged(false);
                } else if (!node.isVisible()) {
                    node.setVisible(true);
                    node.setManaged(true);
                }
            }
        } else {
            for (Node node : content.getChildren()) {
                node.setVisible(true);
                node.setManaged(true);
            }
        }
    }
}
