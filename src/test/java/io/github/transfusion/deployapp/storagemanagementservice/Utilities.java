package io.github.transfusion.deployapp.storagemanagementservice;

import java.io.File;

public class Utilities {
    public static String getResourcesAbsolutePath(String resourceName) {
        ClassLoader classLoader = Utilities.class.getClassLoader();
        File file = new File(classLoader.getResource(resourceName).getFile());
        return file.getAbsolutePath();
    }
}
