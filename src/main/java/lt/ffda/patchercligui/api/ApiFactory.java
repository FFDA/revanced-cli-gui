package lt.ffda.patchercligui.api;

import lt.ffda.patchercligui.util.ApiVersion;
import lt.ffda.patchercligui.util.Preference;
import lt.ffda.patchercligui.util.Preferences;

/**
 * Factory/Singleton class to get class that is compatible with cli API selected settings
 */
public class ApiFactory {
    private static final ApiFactory apiFactory = new ApiFactory();
    private Api api;

    public static ApiFactory getInstance() {
        return apiFactory;
    }

    /**
     * Get object that implements Api interface. If one isn't initiated yet initiates one that implements currently
     * selected Api level in preferences.
     * @return Object that implements Api interface
     */
    public Api getApi() {
        if (api != null) {
            return api;
        }
        String versionString = Preferences.getInstance().getStringPreferenceValue(Preference.API_VERSION);
        if (versionString == null) {
            return api = new ApiV5();
        }
        switch (ApiVersion.valueOf(versionString)) {
            case V4 -> api = new ApiV4();
            case V5 -> api = new ApiV5();
            default -> api = new MorpheApiV1();
        }
        return api;
    }

    /**
     * Switches instance of current api to passed as parameter
     * @param apiVersion ApiVersion to change to
     */
    public void changeApi(ApiVersion apiVersion) {
        switch (apiVersion) {
            case V4 -> api = new ApiV4();
            case V5 -> api = new ApiV5();
            default -> api = new MorpheApiV1();
        }
    }
}
