package io.github.transfusion.deployapp.storagemanagementservice.services.assets;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Constants {
    public enum IPA_ASSET {
        MOBILEPROVISION,
    }

    public enum GENERAL_ASSET {
        PUBLIC_ICON,
        ALL_ICONS,
    }

    public static Set<IPA_ASSET> PRIVATE_IPA_ASSETS = Stream.of(IPA_ASSET.MOBILEPROVISION).collect(Collectors.toCollection(HashSet::new));
    public static Set<String> PRIVATE_IPA_ASSETS_STRING = Stream.of(IPA_ASSET.MOBILEPROVISION.toString()).collect(Collectors.toCollection(HashSet::new));
    // all others assumed to be public

    public enum ASSET_STATUS {
        FAILED,
        SUCCESS
    }
}
