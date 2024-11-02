package lt.ffda.revancedcligui.tasks;

import javafx.concurrent.Task;
import lt.ffda.revancedcligui.util.Preference;

import java.io.FileWriter;
import java.util.Map;

public class WritePreferences extends Task<Void> {
    private final Map<Preference, Object> preferences;

    /**
     * Writes preferences to file
     * @param preferences Map of preferences
     */
    public WritePreferences(Map<Preference, Object> preferences) {
        this.preferences = preferences;
    }

    @Override
    protected Void call() throws Exception {
        FileWriter fileWriter = new FileWriter("conf.prefs");
        for (Preference key : preferences.keySet()) {
            fileWriter.write(String.format("%1$s=%2$s\n", key, preferences.get(key)));
        }
        fileWriter.flush();
        fileWriter.close();
        return null;
    }
}
