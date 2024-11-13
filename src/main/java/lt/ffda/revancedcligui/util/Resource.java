package lt.ffda.revancedcligui.util;

public enum Resource {
    APK_TO_PATCH("Apk to patch", "apk-to-patch", "", "apk"),
    REVANCED_CLI("Revanced CLI","revanced-cli", "https://api.github.com/repos/revanced/revanced-cli/releases", "jar"),
    REVANCED_PATCHES("Revanced patches","revanced-patches", "https://api.github.com/repos/revanced/revanced-patches/releases", "rvp"),
    REVANCED_INTEGRATIONS("Revanced integrations","revanced-integrations", "https://api.github.com/repos/revanced/revanced-integrations/releases", "apk"),
    MICROG("microG", "microg", "https://api.github.com/repos/revanced/gmscore/releases", "apk"),
    ADB("Android Debugging Bridge", "bin", "https://developer.android.com/tools/releases/platform-tools", ""),
    PATCHED_APKS("Patched APKs", "patched-apks", "", "apk");

    private final String name;
    private final String folderName;
    private final String releasesUrl;
    private final String extension;

    Resource(String name, String folderName, String releasesUrl, String extension) {
        this.name = name;
        this.folderName = folderName;
        this.releasesUrl = releasesUrl;
        this.extension = extension;
    }

    /**
     * Get human friendly name of the resource
     * @return resource name
     */
    public String getName() {
        return name;
    }

    /**
     * Get folder name in which resource is stored
     * @return resource folder name
     */
    public String getFolderName() {
        return folderName;
    }

    /**
     * Url to the resource json. For resources from GitHub it should point to releases
     * @return url to json
     */
    public String getReleasesUrl() {
        return releasesUrl;
    }

    /**
     * Returns resource file extension without a dot
     * @return file extension
     */
    public String getExtension() {
        return extension;
    }
}
