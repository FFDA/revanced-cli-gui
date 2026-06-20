package resource;

public enum MorpheResource implements IResource {
    APK_TO_PATCH("Apk to patch", "apk-to-patch", "", "apk"),
    MORPHE_CLI("Morphe CLI","cli", "https://api.github.com/repos/MorpheApp/morphe-cli/releases", "jar"),
    MORPHE_PATCHES("Morphe patches","patches", "https://api.github.com/repos/MorpheApp/morphe-patches/releases", "mpp"),
    MICROG("microG", "microg", "https://api.github.com/repos/MorpheApp/MicroG-RE/releases", "apk"),
    PATCHED_APKS("Patched APKs", "patched-apks", "", "apk");

    private final String name;
    private final String folderName;
    private final String releasesUrl;
    private final String extension;

    MorpheResource(String name, String folderName, String releasesUrl, String extension) {
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
