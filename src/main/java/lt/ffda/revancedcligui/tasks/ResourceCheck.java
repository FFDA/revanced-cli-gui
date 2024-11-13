package lt.ffda.revancedcligui.tasks;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.concurrent.Task;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import lt.ffda.revancedcligui.util.Resource;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.net.URL;
import java.util.Arrays;
import java.util.Comparator;
import java.util.stream.Collectors;

public class ResourceCheck extends Task<Void> {
    private final Resource resource;
    private final TextArea textArea;
    private final ComboBox<String> comboBox;
    private final boolean downloadDevReleases;
    private final ChangeListener<String> changeListener;
    private final ResourceCheckCallback resourceCheckCallback;

    /**
     * Checks for new version of the resource.
     * Downloads it to the resource file if found
     * @param resource Resource enum to check for the new version
     * @param textArea text area where to print messages for the user
     * @param comboBox resource combo box to update if new resource was found
     * @param downloadDevReleases true - do download pre releases
     * @param changeListener pass ChangeListener of the combobox if it has one to disable it
     */
    public ResourceCheck(Resource resource, TextArea textArea, ComboBox<String> comboBox, boolean downloadDevReleases, ChangeListener<String> changeListener, ResourceCheckCallback resourceCheckCallback) {
        this.resource = resource;
        this.textArea = textArea;
        this.comboBox = comboBox;
        this.downloadDevReleases = downloadDevReleases;
        this.changeListener = changeListener;
        this.resourceCheckCallback = resourceCheckCallback;
    }

    /**
     * Checks for new version of the resource.
     * Downloads it to the resource file if found
     * @param resource Resource enum to check for the new version
     * @param textArea text area where to print messages for the user
     * @param comboBox resource combo box to update if new resource was found
     * @param downloadDevReleases true - do download pre releases
     */
    public ResourceCheck(Resource resource, TextArea textArea, ComboBox<String> comboBox, boolean downloadDevReleases, ResourceCheckCallback resourceCheckCallback) {
        this(resource, textArea, comboBox, downloadDevReleases, null, resourceCheckCallback);
    }

    @Override
    protected Void call() {
        try {
            InputStream inputStream = new URL(resource.getReleasesUrl()).openStream();
            String jsonString = new String(inputStream.readAllBytes());
            inputStream.close();
            if (jsonString.isEmpty()) {
                return null;
            }
            JSONObject releaseJson = null;
            String filename = null;
            JSONArray releasesArray = new JSONArray(jsonString);
            for (int i = 0; i < releasesArray.length(); i++) {
                if (!this.downloadDevReleases && releasesArray.getJSONObject(i).getBoolean("prerelease")) {
                    continue;
                }
                releaseJson = getReleaseJsonObject(resource, releasesArray.getJSONObject(i).getJSONArray("assets"));
                if (resource == Resource.MICROG) {
                    filename = String.format("microg_%1$s.apk", releasesArray.getJSONObject(i).getString("tag_name"));
                } else {
                    filename = releaseJson.getString("name");
                }
                break;
            }
            if (releaseJson == null || filename == null) {
                textArea.appendText(String.format("Failed to download %1$s resource. Try enabling \"Download pre-releases\"", resource.getName()));
                return null;
            }
            String finalFilename = filename;
            String downloadUrl = releaseJson.getString("browser_download_url");
            File newRelease = new File(resource.getFolderName() + File.separatorChar + finalFilename);
            // Tries to create new file. If it fails it means that file already exists
            if (newRelease.createNewFile()) {
                Platform.runLater(() -> textArea.appendText(String.format("Found a new version of %1$s. Downloading. Filename: %2$s\n", resource.getName(), finalFilename)));
                downloadNewResource(downloadUrl, newRelease);
                Platform.runLater(() -> textArea.appendText(String.format("Finished downloading %1$s\n", finalFilename)));
                updateComboBox();
            } else {
                Platform.runLater(() -> textArea.appendText(String.format("%1$s is already at the latest version\n", resource.getName())));
            }
        } catch (IOException e) {
            this.textArea.appendText(String.format("Exception while downloading %1$s\n", resource.getName()));
        }
        return null;
    }

    /**
     * Downloading the resource
     * @param downloadUrl url to the resource
     * @param file file to which write the resource into
     * @throws IOException exception while opening the streams
     */
    private void downloadNewResource(String downloadUrl, File file) throws IOException {
        InputStream inputStream = new URL(downloadUrl).openStream();
        OutputStream outputStream = new FileOutputStream(file);
        outputStream.write(inputStream.readAllBytes());
        inputStream.close();
        outputStream.close();
    }

    /**
     * Updates file list of the combobox. Disables combobox listener if one was passed as an argument.
     */
    private void updateComboBox() {
        Platform.runLater(() -> {
            if (this.changeListener != null) {
                this.comboBox.getSelectionModel().selectedItemProperty().removeListener(this.changeListener);
            }
            this.comboBox.getItems().setAll(
                    Arrays.stream(new File(resource.getFolderName()).list())
                            .sorted(Comparator.reverseOrder())
                            .collect(Collectors.toList())
            );
            if (this.changeListener != null) {
                this.comboBox.getSelectionModel().selectedItemProperty().addListener(this.changeListener);
            }
            this.comboBox.getSelectionModel().select(0);
            if (this.resourceCheckCallback != null) {
                this.resourceCheckCallback.callback();
            }
        });
    }

    /**
     * Returns first object from assets that has the same extension as resource
     * @param resource resource that
     * @param assets assets from release
     * @return JSONObject with resource that can be downloaded, null - not found
     */
    private JSONObject getReleaseJsonObject(Resource resource, JSONArray assets) {
        JSONObject object = null;
        for (int i = 0; i < assets.length(); i++) {
            String filename = assets.getJSONObject(i).getString("name");
            int index = filename.lastIndexOf(".");
            if (index >= 0) {
                if (resource.getExtension().equals(filename.substring(index + 1))) {
                    object = assets.getJSONObject(i);
                    break;
                }
            }
        }
        return object;
    }
}
