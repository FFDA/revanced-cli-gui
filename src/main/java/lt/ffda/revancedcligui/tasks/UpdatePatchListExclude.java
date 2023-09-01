package lt.ffda.revancedcligui.tasks;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import lt.ffda.revancedcligui.dto.PatchDto;
import lt.ffda.revancedcligui.util.CreateUiElement;

import java.util.ArrayList;

public class UpdatePatchListExclude extends Task<Void> {
    private final VBox content;
    private final ArrayList<PatchDto> patches;
    private final String packageName;

    /**
     * Updates showed list to only display patches that have packageName in the package list
     * @param content UI element that shows patches to user
     * @param patches PatchDto object list of all available patches
     * @param packageName name of the package that should be sorted out and displayed
     */
    public UpdatePatchListExclude(VBox content, ArrayList<PatchDto> patches, String packageName) {
        this.content = content;
        this.patches = patches;
        this.packageName = packageName;
    }
    
    @Override
    protected Void call() {
        ArrayList<HBox> newPatchList = new ArrayList<>();
        patches.forEach(p -> {
            if (p.getPackageName().contains(packageName))
                newPatchList.add(CreateUiElement.patchLine(p.getName(), p.getDescription()));
        });
        Platform.runLater(() -> {
            this.content.getChildren().clear();
            this.content.getChildren().addAll(newPatchList);
        });
        return null;
    }
}
