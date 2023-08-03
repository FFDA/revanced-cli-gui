package lt.ffda.revancedcligui.tasks;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ListPatches extends Task<Void> {
    private final String command;
    private final VBox content;
    private ArrayList<HBox> patches = new ArrayList<>();

    /**
     * Creates a list of all available patches and adds them to provided VBox
     * @param command command to get all available patches
     * @param content VBox that should contain available patch list
     */
    public ListPatches(String command, VBox content) {
        this.command = command;
        this.content = content;
    }

    @Override
    protected Void call() throws Exception {
        Platform.runLater(() -> this.content.getChildren().clear());
        Runtime runtime = Runtime.getRuntime();
        Process process = runtime.exec(this.command);
        BufferedReader stdInput = new BufferedReader(new InputStreamReader(process.getInputStream()));
        Pattern pattern = Pattern.compile("INFO:\\s+([\\w\\-]+)\\s(.+)$");
        String line;
        while ((line = stdInput.readLine()) != null) {
            Matcher matcher = pattern.matcher(line);
            if (matcher.find()) {
                this.createPatchLine(matcher.group(1), matcher.group(2));
            }
        }
        stdInput.close();
        Platform.runLater(() -> this.content.getChildren().addAll(this.patches));
        return null;
    }

    /**
     * Creates one line of the available patch list
     * @param name name of the patch
     * @param description description of the patch
     */
    private void createPatchLine(String name, String description) {
        HBox hBox = new HBox();
        hBox.getStyleClass().add("patch-list-item");
        CheckBox checkBox = new CheckBox(name);
        Label label = new Label(String.format(" - %1$s", description));
        hBox.getChildren().addAll(checkBox, label);
        this.patches.add(hBox);
    }
}
