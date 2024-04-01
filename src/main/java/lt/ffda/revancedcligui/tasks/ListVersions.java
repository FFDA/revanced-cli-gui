package lt.ffda.revancedcligui.tasks;

import javafx.concurrent.Task;
import javafx.scene.control.TextArea;
import lt.ffda.revancedcligui.util.Resource;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;

/**
 * Uses revanced-cli command to list all supported YouTube apk versions
 */
public class ListVersions extends Task<Void> {
    private final TextArea textArea;
    private final String cli;
    private final String patches;
    private final String command;

    /**
     * Collects and print all supported YouTube apk version to provided TextArea. Supported versions might depend on
     * revanced-cli and revanced-patches version that is being used.
     * @param textArea text area to print the result
     * @param cli cli filename that will be used to get suppoerted versions
     * @param patches patches filename that will be used to the supported versions
     */
    public ListVersions(TextArea textArea, String cli, String patches) {
        this.textArea = textArea;
        this.cli = cli;
        this.patches = patches;
        this.command = getCommand();
    }

    @Override
    protected Void call() throws Exception {
        Runtime runtime = Runtime.getRuntime();
        Process process = runtime.exec(this.command);
        BufferedReader stdInput = new BufferedReader(new InputStreamReader(process.getInputStream()));
        StringBuilder output = new StringBuilder("Supported YouTube apk versions: ");
        String line;
        while ((line = stdInput.readLine()) != null) {
            if (line.contains("patches")) {
                output.append(line.trim());
                output.append(", ");
            }
        }
        output.delete(output.length() - 2, output.length());
        output.append('\n');
        this.textArea.appendText(output.toString());
        return null;
    }

    /**
     * Creates command that prints suppoerted versions of YouTube by Revanced
     * @return command can be run to get supported YouTube versions
     */
    private String getCommand() {
        StringBuilder command = new StringBuilder("java -jar ");
        command.append(Resource.REVANCED_CLI.getFolderName())
                .append(File.separatorChar)
                .append(this.cli)
                .append(" list-versions")
                .append(" -f com.google.android.youtube -u ")
                .append(Resource.REVANCED_PATCHES.getFolderName())
                .append(File.separatorChar)
                .append(this.patches);
        return command.toString();
    }
}
