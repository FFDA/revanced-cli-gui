package lt.ffda.revancedcligui.api;

import lt.ffda.revancedcligui.util.ApiVersion;
import lt.ffda.revancedcligui.util.Preference;
import lt.ffda.revancedcligui.util.Preferences;

/**
 * Factory/Singleton class to get class that is compatible with ReVanced API selected settings
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
        switch (ApiVersion.valueOf(Preferences.getInstance().getStringPreferenceValue(Preference.API_VERSION))) {
            case V4 -> api = new ApiV4();
            default -> api = new ApiV5();
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
            default -> api = new ApiV5();
        }
    }
}
