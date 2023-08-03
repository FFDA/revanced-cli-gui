package lt.ffda.revancedcligui.util;

import java.io.*;

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
        if (Preferences.getInstance().getPreferenceValue(Preference.USE_EMBEDDED_ADB)) {
            if (System.getProperty("os.name").equals("Linux")) {
                adb = Resource.ADB.getFolderName() + File.separatorChar + "adb";
            } else {
                adb = Resource.ADB.getFolderName() + File.separatorChar + "adb.exe";
            }
        } else {
            if (System.getProperty("os.name").equals("Linux")) {
                adb = "adb";
            } else {
                adb = "adb.exe";
            }
        }
    }

    /**
     * Saves embedded adb executable to bin folder
     * For Linux it also adds permissions to execute the binary
     */
    public void saveAdb() {
        new File(Resource.ADB.getFolderName()).mkdir();
        try {
            File outputFile;
            if (System.getProperty("os.name").equals("Linux")) {
                outputFile = new File(Resource.ADB.getFolderName() + File.separatorChar + "adb");
                if (!outputFile.createNewFile()) {
                    // File already exists
                    return;
                }
                this.writeStream(
                        Adb.class.getResource("/adb").openStream(),
                        new FileOutputStream(outputFile)
                );
                Runtime.getRuntime().exec(String.format("chmod +x %1$s", Resource.ADB.getFolderName() + File.separatorChar + "adb"));
            } else {
                outputFile = new File(Resource.ADB.getFolderName() + File.separatorChar + "adb.exe");
                if (!outputFile.createNewFile()) {
                    // File already exists
                    return;
                }
                this.writeStream(
                        Adb.class.getResource("/adb.exe").openStream(),
                        new FileOutputStream(outputFile)
                );
                this.writeStream(
                        Adb.class.getResource("/AdbWinApi.dll").openStream(),
                        new FileOutputStream(Resource.ADB.getFolderName() + File.separatorChar + "AdbWinApi.dll"));
                this.writeStream(
                        Adb.class.getResource("/AdbWinUsbApi.dll").openStream(),
                        new FileOutputStream(Resource.ADB.getFolderName() + File.separatorChar + "AdbWinUsbApi.dll"));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Write output stream to input stream and closes them
     * @param inputStream input stream of the file to read
     * @param outputStream output stream of the file to write
     */
    private void writeStream(InputStream inputStream, OutputStream outputStream) throws IOException {
        outputStream.write(inputStream.readAllBytes());
        inputStream.close();
        outputStream.close();
    }

    /**
     * Returns adb from platform-tools_r34.0.4
     * @return adb file embedded in resources
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
