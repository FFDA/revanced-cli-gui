package lt.ffda.revancedcligui.tasks;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.control.TextArea;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Patcher extends Task<Void> {
    private final TextArea textArea;
    private final ProcessBuilder processBuilder;

    /**
     * Runs provided command
     * @param command command to use
     * @param textArea text area to print messages for the user
     */
    public Patcher(String command, TextArea textArea) {
        this.textArea = textArea;
        this.processBuilder = new ProcessBuilder(command.trim().split("\\s+"))
                .redirectErrorStream(true);
        exceptionProperty().addListener((observable, oldException, newException) -> {
            if (newException != null) {
                newException.printStackTrace();
            }
        });
    }

    @Override
    public Void call() throws IOException {
        Process process = this.processBuilder.start();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                final String finalLine = line;
                Platform.runLater(() -> this.textArea.appendText(finalLine + "\n"));
            }
        }
        return null;
    }
}
