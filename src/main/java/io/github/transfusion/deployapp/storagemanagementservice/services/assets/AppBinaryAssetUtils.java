package io.github.transfusion.deployapp.storagemanagementservice.services.assets;

public class AppBinaryAssetUtils {
    public static boolean isAssetPrivate(String asset) {
        return Constants.PRIVATE_IPA_ASSETS_STRING.contains(asset);
    }
}
