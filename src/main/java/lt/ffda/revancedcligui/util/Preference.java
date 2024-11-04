package lt.ffda.revancedcligui.util;

/**
 * Available preferences. Preferences can be of two values boolean or string
 */
public enum Preference {
    USE_EMBEDDED_ADB(0),
    DOWNLOAD_DEV_RELEASES(0),
    CLEAN_TEMPORARY_FILES(0),
    PRINT_SUPPORTED_VERSIONS(0),
    INSTALL_AFTER_PATCH(0),
    USE_KEYSTORE_FILE(0),
    PRINT_PATCH_COMMAND(0),
    API_VERSION(1);

    private final Integer type;

    /**
     * Constructor for the preference
     * @param type 0 for boolean, 1 for string
     */
    Preference(Integer type) {
        this.type = type;
    }

    /**
     * Get the type of the preference
     * @return 0 - boolean, 1 - string
     */
    public Integer getType() {
        return type;
    }
}
