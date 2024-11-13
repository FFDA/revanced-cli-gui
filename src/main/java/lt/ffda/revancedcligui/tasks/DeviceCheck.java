package lt.ffda.revancedcligui.tasks;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import lt.ffda.revancedcligui.util.Adb;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Checks connected ADB devices and list them in Combobox
 */
public class DeviceCheck extends Task<Void> {
    private final ComboBox<String> devices;
    private final TextArea textArea;

    public DeviceCheck(ComboBox<String> devices, TextArea textArea) {
        this.devices = devices;
        this.textArea = textArea;
    }

    @Override
    protected Void call() {
        Runtime runtime = Runtime.getRuntime();
        try {
            Platform.runLater(() -> this.textArea.appendText("Searching for connected devices\n"));
            List<String> devices = new ArrayList<>();
            Process process = runtime.exec(Adb.getInstance().getAdb() + " devices -l");
            BufferedReader stdInput = new BufferedReader(new InputStreamReader(process.getInputStream()));
            Pattern pattern = Pattern.compile("(\\S+)\\s+.+model:([\\S]+) device:([\\S]+)");
            String line;
            while ((line = stdInput.readLine()) != null) {
                Matcher matcher = pattern.matcher(line);
                if (matcher.find()) {
                    devices.add(String.format("%1$s - %2$s %3$s", matcher.group(1), matcher.group(2), matcher.group(3)));
                }
            }
            if (devices.size() == 0) {
                Platform.runLater(() -> this.textArea.appendText("No connected device was found\n"));
                return null;
            }
            if (devices.size() == 1) {
                Platform.runLater(() -> {
                    this.devices.getItems().setAll(devices);
                    this.devices.getSelectionModel().select(0);
                });
            } else {
                Platform.runLater(() -> this.devices.getItems().setAll(devices));
            }
            Platform.runLater(() -> this.textArea.appendText(String.format("Found %1$d device(s)\n", devices.size())));
            stdInput.close();
        } catch (IOException e) {
            Platform.runLater(() -> this.textArea.appendText(e.getMessage()));
        }
        return null;
    }
}