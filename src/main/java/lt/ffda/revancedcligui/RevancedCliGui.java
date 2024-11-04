package lt.ffda.revancedcligui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import lt.ffda.revancedcligui.util.Adb;
import lt.ffda.revancedcligui.util.Preference;
import lt.ffda.revancedcligui.util.Preferences;
import lt.ffda.revancedcligui.util.Resource;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class RevancedCliGui extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        createFolderStructure();
        FXMLLoader fxmlLoader = new FXMLLoader(RevancedCliGui.class.getResource("view/main-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 800, 650);
        stage.setTitle(String.format("Revanced-CLI-GUI v%1$s", getVersion()));
        stage.setScene(scene);
        stage.getIcons().addAll(
                new Image(RevancedCliGui.class.getResource("icons/icon-16.png").toString()),
                new Image(RevancedCliGui.class.getResource("icons/icon-32.png").toString()),
                new Image(RevancedCliGui.class.getResource("icons/icon-64.png").toString()),
                new Image(RevancedCliGui.class.getResource("icons/icon-128.png").toString())
        );
        stage.setMinHeight(650);
        stage.setMinWidth(800);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }

    /**
     * Creates folder structure for program to store files
     */
    private void createFolderStructure() {
        new File(Resource.YOUTUBE_APK.getFolderName()).mkdir();
        new File(Resource.REVANCED_CLI.getFolderName()).mkdir();
        new File(Resource.REVANCED_PATCHES.getFolderName()).mkdir();
        new File(Resource.REVANCED_INTEGRATIONS.getFolderName()).mkdir();
        new File(Resource.MICROG.getFolderName()).mkdir();
        new File(Resource.PATCHED_APKS.getFolderName()).mkdir();
        if (Preferences.getInstance().getBooleanPreferenceValue(Preference.USE_EMBEDDED_ADB)) {
            Adb.getInstance().saveAdb();
        }
    }

    @Override
    public void stop() throws Exception {
        Adb.getInstance().killAdbServer();
        super.stop();
    }

    private String getVersion() throws IOException {
        String version = null;
        Properties properties = new Properties();
        InputStream input = getClass().getResourceAsStream("/config.properties");
        if (input != null) {
            properties.load(input);
            version = properties.getProperty("version");
        }
        return version;
    }
}