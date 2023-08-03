package lt.ffda.revancedcligui.tasks;

import javafx.concurrent.Task;

import java.io.FileWriter;
import java.util.Map;

public class WritePreferences extends Task<Void> {
    private final Map<String, Byte> preferences;

    /**
     * Writes preferences to file
     * @param preferences Map of preferences
     */
    public WritePreferences(Map<String, Byte> preferences) {
        this.preferences = preferences;
    }

    @Override
    protected Void call() throws Exception {
        FileWriter fileWriter = new FileWriter("conf.prefs");
        for (String key : this.preferences.keySet()) {
            fileWriter.write(String.format("%1$s=%2$d\n", key, this.preferences.get(key)));
        }
        fileWriter.flush();
        fileWriter.close();
        return null;
    }
}
