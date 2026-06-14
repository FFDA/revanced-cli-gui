package lt.ffda.revancedcligui.api;

import lt.ffda.revancedcligui.util.ApiVersion;
import resource.IResource;
import resource.MorpheResource;

import java.io.File;

public class MorpheApiV1 extends ApiV5 {

    @Override
    public ApiVersion getApiVersion() {
        return ApiVersion.MORPHE_V1;
    }

    @Override
    public String getListPatchesCommand() {
        return "java -jar %1$s list-patches -p --patches %2$s";
    }

    public String getSupportedVersionCommand(String cli, String patches, String packageName) {
        Api api = ApiFactory.getInstance().getApi();
        StringBuilder command = new StringBuilder("java -jar ");
        command.append(api.getCliResource().getFolderName())
                .append(File.separatorChar)
                .append(cli)
                .append(" list-versions")
                .append(" -f ")
                .append(packageName)
                .append(" --patches ")
                .append(api.getPatchesResource().getFolderName())
                .append(File.separatorChar)
                .append(patches);
        return command.toString();
    }

    @Override
    public IResource getApkToPatchResource() {
        return MorpheResource.APK_TO_PATCH;
    }

    @Override
    public IResource getCliResource() {
        return MorpheResource.MORPHE_CLI;
    }

    @Override
    public IResource getPatchesResource() {
        return MorpheResource.MORPHE_PATCHES;
    }

    @Override
    public IResource getIntegrationsResource() {
        return null;
    }

    @Override
    public IResource getMicroGResource() {
        return MorpheResource.MICROG;
    }

    @Override
    public IResource getAdbResource() {
        return MorpheResource.ADB;
    }

    @Override
    public IResource getPatchedApksResource() {
        return MorpheResource.PATCHED_APKS;
    }

}
