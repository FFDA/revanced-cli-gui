package lt.ffda.revancedcligui.dto;

import java.util.ArrayList;

public class PatchDto {
    private String name;
    private String description;
    private final ArrayList<String> packageName;

    /**
     * PatchDto object holds all information about available patches collected from revanced-cli and a patch apk
     * @param name name of the patch
     */
    public PatchDto(String name) {
        this.name = name;
        this.packageName = new ArrayList<>();
    }

    /**
     * Get name of the patch
     * @return name of the patch
     */
    public String getName() {
        return name;
    }

    /**
     * Set name of the patch
     * @param name name of the patch
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Get patch description
     * @return patch description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Set patch description
     * @param description patch description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Get list of packages the patch can be applied to
     * @return list of packages
     */
    public ArrayList<String> getPackageName() {
        return packageName;
    }
}
