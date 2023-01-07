package io.github.transfusion.deployapp.storagemanagementservice.interop;

import io.github.transfusion.app_info_java_graalvm.AppInfo.IPA;
import io.github.transfusion.deployapp.storagemanagementservice.config.GraalPolyglotConfig;
import io.github.transfusion.deployapp.storagemanagementservice.mappers.AppDetailsMapper;
import io.github.transfusion.deployapp.storagemanagementservice.mappers.AppDetailsMapperImpl;
import org.graalvm.polyglot.Context;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Import;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.UUID;

import static io.github.transfusion.deployapp.storagemanagementservice.Utilities.getResourcesAbsolutePath;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

//@Import(value = {AppDetailsMapper.class})
@ExtendWith({SpringExtension.class, MockitoExtension.class})
@Import({Jackson2ObjectMapperBuilder.class, GraalPolyglotConfig.class})
@ContextConfiguration(classes = {
        AppDetailsMapperImpl.class,
})
public class AppParsingRegressionTest {

    @Autowired
    private AppDetailsMapper appDetailsMapper;

    @Autowired
    @Qualifier("polyglotContext")
    private Context polyglotCtx;

    @Test
    public void infoPlistWithBinaryData() {
        UUID uuid = UUID.fromString("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa");
        UUID storageCredentialId = UUID.fromString("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaab");

        Context ctx = polyglotCtx;
        String resourceName = "apps/NineAnimator_1.2.7_1672916973.ipa";
        String absolutePath = getResourcesAbsolutePath(resourceName);
        IPA subject = IPA.from(ctx, absolutePath);

        assertDoesNotThrow(() -> appDetailsMapper.mapPolyglotIPAtoIpa(subject, uuid, storageCredentialId, "sample.filename"));
    }
}
