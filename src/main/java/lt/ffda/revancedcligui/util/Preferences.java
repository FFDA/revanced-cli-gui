package lt.ffda.revancedcligui.util;

import lt.ffda.revancedcligui.tasks.WritePreferences;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

/**
 * Preference singleton. Saves preferences in the same folder as all other files for portability
 * All preferences are booleans
 */
public class Preferences {
    private final static Preferences instance = new Preferences();
    private final Map<String, Byte> preferences = Collections.synchronizedMap(new HashMap<>());

    private Preferences() {
        try {
            File file = new File("conf.prefs");
            if (file.createNewFile()) {
                this.initNewConf(file);
            }
            Scanner scanner = new Scanner(file);
            while (scanner.hasNextLine()) {
                String[] preference = scanner.nextLine().split("=");
                this.preferences.put(preference[0], Byte.valueOf(preference[1]));
            }
            scanner.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static Preferences getInstance() {
        return instance;
    }

    /**
     * If program was launched for the first time or conf.prefs file is missing
     * @param file file to write the preferences into
     */
    private void initNewConf(File file) {
        try {
            FileWriter fileWriter = new FileWriter(file);
            fileWriter.write(String.format("%1$s=%2$d\n", Preference.USE_EMBEDDED_ADB, 0));
            fileWriter.write(String.format("%1$s=%2$d\n", Preference.DOWNLOAD_DEV_RELEASES, 0));
            fileWriter.write(String.format("%1$s=%2$d\n", Preference.CLEAN_TEMPORARY_FILES, 0));
            fileWriter.flush();
            fileWriter.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Initiates writing of current preferences
     */
    private void writePreferences() {
        new Thread(new WritePreferences(this.preferences)).start();
    }

    /**
     * Returns preference value of the provided preference
     * @param preference preference of which value to return
     * @return true - preference is enabled, else - false
     */
    public boolean getPreferenceValue(Preference preference) {
        return this.preferences.get(preference.name()) == 1;
    }

    /**
     * Sets preference to the provided boolean value
     * @param preference preference of which value has to be change
     * @param value value of the preference
     */
    public void setPreferenceValue(Preference preference, boolean value) {
        this.preferences.put(preference.name(), value ? (byte) 1 : (byte) 0);
        this.writePreferences();
    }
}
