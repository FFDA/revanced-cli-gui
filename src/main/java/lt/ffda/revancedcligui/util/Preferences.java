package lt.ffda.revancedcligui.util;

import lt.ffda.revancedcligui.tasks.WritePreferences;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

/**
 * Preference singleton. Saves preferences in the same folder as all other files for portability
 */
public class Preferences {
    private final static Preferences instance = new Preferences();
    private final Map<Preference, Object> preferences = Collections.synchronizedMap(new HashMap<>());

    private Preferences() {
        try {
            File file = new File("conf.prefs");
            if (file.createNewFile()) {
                this.initNewConf(file);
            }
            Scanner scanner = new Scanner(file);
            while (scanner.hasNextLine()) {
                String[] preference = scanner.nextLine().split("=");
                preferences.put(Preference.valueOf(preference[0]), preference[1]);
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
            fileWriter.write(String.format("%1$s=%2$d\n", Preference.PRINT_SUPPORTED_VERSIONS, 1));
            fileWriter.write(String.format("%1$s=%2$d\n", Preference.INSTALL_AFTER_PATCH, 0));
            fileWriter.write(String.format("%1$s=%2$s\n", Preference.API_VERSION, ApiVersion.V4));
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
        new Thread(new WritePreferences(preferences)).start();
    }

    /**
     * Returns boolean value of the provided preference if it's set in the conf.prefs file
     * @param preference preference of which value to return
     * @return true - preference is enabled, false - preference is disabled, it does not exist in the file, or it's a string preference
     */
    public boolean getBooleanPreferenceValue(Preference preference) {
        if (preference.getType() == 1) {
            return false;
        }
        if (preferences.containsKey(preference)) {
            return Integer.valueOf(1).equals(preferences.get(preference));
        } else {
            return false;
        }
    }

    /**
     * Sets preference to the provided boolean value. Writes changes to file.
     * @param preference preference of which value has to be change
     * @param value value of the preference
     */
    public void setBooleanPreferenceValue(Preference preference, boolean value) {
        preferences.put(preference, value ? (byte) 1 : (byte) 0);
        writePreferences();
    }

    /**
     * Returns string value of the provided preference if it exists in the file
     * @param preference preference of which value to return
     * @return string value of the preference, null - if it does not exist in conf.prefs file, or it is boolean preference
     */
    public String getStringPreferenceValue(Preference preference) {
        if (preference.getType() == 0) {
            return null;
        }
        if (preferences.containsKey(preference)) {
            return (String) preferences.get(preference);
        } else {
            return null;
        }
    }

    /**
     * Sets preference to the provided string value. Writes changes to file.
     * @param preference preference to change/set value for
     * @param value value of the preference to set
     */
    public void setStringPreferenceValue(Preference preference, String value) {
        preferences.put(preference, value);
        writePreferences();
    }
}
