package resource;

/**
 * Interface that defines folders where to store downloaded resources. Resource names, urls and extensions.
 */
public interface IResource {

    /**
     * Get human friendly name of the resource
     * @return resource name
     */
    public String getName();

    /**
     * Get folder name in which resource is stored
     * @return resource folder name
     */
    public String getFolderName();

    /**
     * Url to the resource json. For resources from GitHub it should point to releases
     * @return url to json
     */
    public String getReleasesUrl();

    /**
     * Returns resource file extension without a dot
     * @return file extension
     */
    public String getExtension();
}
