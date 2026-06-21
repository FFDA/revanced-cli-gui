package lt.ffda.patchercligui.util;

/**
 * Supported ReVanced-CLI and Morphe-CLI API versions
 */
public enum ApiVersion {
    V4, V5, MORPHE_V1;

    public static final ApiVersion DEFAULT_API = ApiVersion.MORPHE_V1;
}
