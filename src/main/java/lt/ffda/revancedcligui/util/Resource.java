package lt.ffda.revancedcligui.util;

public enum Resource {
    YOUTUBE_APK("Youtube apk", "youtube-apk", ""),
    REVANCED_CLI("Revanced CLI","revanced-cli", "https://api.github.com/repos/revanced/revanced-cli/releases"),
    REVANCED_PATCHES("Revanced patches","revanced-patches", "https://api.github.com/repos/revanced/revanced-patches/releases"),
    REVANCED_INTEGRATIONS("Revanced integrations","revanced-integrations", "https://api.github.com/repos/revanced/revanced-integrations/releases"),
    MICROG("microG", "microg", "https://api.github.com/repos/revanced/gmscore/releases"),
    ADB("Android Debugging Bridge", "bin", "https://developer.android.com/tools/releases/platform-tools");

    private final String name;
    private final String folderName;
    private final String releasesUrl;

    Resource(String name, String folderName, String releasesUrl) {
        this.name = name;
        this.folderName = folderName;
        this.releasesUrl = releasesUrl;
    }

    /**
     * Get human friendly name of the resource
     * @return resource name
     */
    public String getName() {
        return this.name;
    }

    /**
     * Get folder name in which resource is stored
     * @return resource folder name
     */
    public String getFolderName() {
        return this.folderName;
    }

    /**
     * Url to the resource json. For resources from GitHub it should point to releases
     * @return url to json
     */
    public String getReleasesUrl() {
        return this.releasesUrl;
    }
}
