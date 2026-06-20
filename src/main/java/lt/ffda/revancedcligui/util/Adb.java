package lt.ffda.revancedcligui.util;

import java.io.IOException;

public class Adb {
    private final static Adb instance = new Adb();
    private static String adb;

    /**
     * Depending on OS creates Android Debugging Bridge file object
     */
    private Adb() {
        this.initAdb();
    }

    public static Adb getInstance() {
        return instance;
    }

    /**
     * Creates path to ADB executable depending on OS and if program should use embedded one or not
     */
    public void initAdb() {
        if (System.getProperty("os.name").equals("Linux")) {
            adb = "adb";
        } else {
            adb = "adb.exe";
        }
    }

    /**
     * Get OS specific adb file. adb for Linux, adb.exe for windows
     * @return OS specific adb file
     */
    public String getAdb() {
        return adb;
    }

    /**
     * Kills adb server
     */
    public void killAdbServer() {
        try {
            Runtime.getRuntime().exec(Adb.getInstance().getAdb() + " kill-server");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
