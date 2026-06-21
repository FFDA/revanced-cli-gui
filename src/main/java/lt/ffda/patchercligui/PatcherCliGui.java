package lt.ffda.patchercligui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import lt.ffda.patchercligui.api.Api;
import lt.ffda.patchercligui.api.ApiFactory;
import lt.ffda.patchercligui.util.Adb;
import lt.ffda.patchercligui.util.ApiVersion;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PatcherCliGui extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        createFolderStructure();
        FXMLLoader fxmlLoader = new FXMLLoader(PatcherCliGui.class.getResource("view/main-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 800, 650);
        stage.setTitle(String.format("Patcher-CLI-GUI v%1$s", getVersion()));
        stage.setScene(scene);
        stage.getIcons().addAll(
                new Image(PatcherCliGui.class.getResource("icons/icon-16.png").toString()),
                new Image(PatcherCliGui.class.getResource("icons/icon-32.png").toString()),
                new Image(PatcherCliGui.class.getResource("icons/icon-64.png").toString()),
                new Image(PatcherCliGui.class.getResource("icons/icon-128.png").toString())
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
        Api api = ApiFactory.getInstance().getApi();
        new File(api.getApkToPatchResource().getFolderName()).mkdir();
        new File(api.getCliResource().getFolderName()).mkdir();
        new File(api.getPatchesResource().getFolderName()).mkdir();
        if (api.getApiVersion() == ApiVersion.V4) {
            new File(api.getIntegrationsResource().getFolderName()).mkdir();
        }
        new File(api.getMicroGResource().getFolderName()).mkdir();
        new File(api.getPatchedApksResource().getFolderName()).mkdir();
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