package lt.ffda.revancedcligui.api;

import resource.IResource;

public interface IApiResources {

    IResource getApkToPatchResource();
    IResource getCliResource();
    IResource getPatchesResource();

    @Deprecated
    /**
     * Removed as of Revanced CLI V5 Api
     */
    IResource getIntegrationsResource();
    IResource getMicroGResource();
    IResource getAdbResource();
    IResource getPatchedApksResource();
}
