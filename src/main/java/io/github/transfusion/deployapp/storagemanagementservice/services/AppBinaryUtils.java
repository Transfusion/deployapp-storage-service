package io.github.transfusion.deployapp.storagemanagementservice.services;

import org.springframework.core.io.Resource;
import org.springframework.util.FileCopyUtils;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UncheckedIOException;

import static java.nio.charset.StandardCharsets.UTF_8;

public class AppBinaryUtils {
    /**
     * Used in ITMS plist generation
     *
     * @param resource a resource on the classpath
     * @return the contents of said resource
     */
    public static String resourceAsString(Resource resource) {
        try (Reader reader = new InputStreamReader(resource.getInputStream(), UTF_8)) {
            return FileCopyUtils.copyToString(reader);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

}
