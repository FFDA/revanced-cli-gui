package resource;

public enum RevancedResource implements IResource {
    APK_TO_PATCH("Apk to patch", "apk-to-patch", "", "apk"),
    REVANCED_CLI("Revanced CLI","cli", "https://api.github.com/repos/revanced/revanced-cli/releases", "jar"),
    REVANCED_PATCHES("Revanced patches","patches", "https://api.github.com/repos/revanced/revanced-patches/releases", "rvp"),
    REVANCED_INTEGRATIONS("Revanced integrations","revanced-integrations", "https://api.github.com/repos/revanced/revanced-integrations/releases", "apk"),
    MICROG("microG", "microg", "https://api.github.com/repos/revanced/gmscore/releases", "apk"),
    ADB("Android Debugging Bridge", "bin", "https://developer.android.com/tools/releases/platform-tools", ""),
    PATCHED_APKS("Patched APKs", "patched-apks", "", "apk");

    private final String name;
    private final String folderName;
    private final String releasesUrl;
    private final String extension;

    RevancedResource(String name, String folderName, String releasesUrl, String extension) {
        this.name = name;
        this.folderName = folderName;
        this.releasesUrl = releasesUrl;
        this.extension = extension;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getFolderName() {
        return folderName;
    }

    @Override
    public String getReleasesUrl() {
        return releasesUrl;
    }

    @Override
    public String getExtension() {
        return extension;
    }
}
