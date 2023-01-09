package io.github.transfusion.deployapp.storagemanagementservice.isolated_integration;

import io.github.transfusion.deployapp.Constants;
import io.github.transfusion.deployapp.session.SessionData;
import io.github.transfusion.deployapp.storagemanagementservice.WithMockCustomUser;
import io.github.transfusion.deployapp.storagemanagementservice.config.GraalPolyglotConfig;
import io.github.transfusion.deployapp.storagemanagementservice.db.entities.AppBinary;
import io.github.transfusion.deployapp.storagemanagementservice.db.entities.MockCredential;
import io.github.transfusion.deployapp.storagemanagementservice.db.repositories.StorageCredentialRepository;
import io.github.transfusion.deployapp.storagemanagementservice.db.specifications.AppBinaryFilterCriteria;
import io.github.transfusion.deployapp.storagemanagementservice.db.specifications.AppBinaryFilterSpecification;
import io.github.transfusion.deployapp.storagemanagementservice.mappers.StorageCredentialMapperImpl;
import io.github.transfusion.deployapp.storagemanagementservice.services.AppBinaryJobService;
import io.github.transfusion.deployapp.storagemanagementservice.services.AppBinaryService;
import io.github.transfusion.deployapp.storagemanagementservice.services.StorageCredsUpdateService;
import io.github.transfusion.deployapp.storagemanagementservice.services.StorageService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.flyway.FlywayAutoConfiguration;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureDataJpa;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureTestEntityManager;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.web.reactive.function.client.WebClient;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.collection.IsIn.isIn;

import java.io.File;
import java.time.Instant;
import java.util.UUID;
import java.util.stream.Collectors;

import static io.github.transfusion.deployapp.storagemanagementservice.Utilities.getResourcesAbsolutePath;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ActiveProfiles("db-test")
@ExtendWith({SpringExtension.class, /*MockitoExtension.class*/})
//@DataJpaTest
@WebAppConfiguration
@EnableAutoConfiguration(exclude = {FlywayAutoConfiguration.class, RedisAutoConfiguration.class})
@AutoConfigureDataJpa
@AutoConfigureTestEntityManager
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
//@AutoConfigureCache
//@AutoConfigureDataJpa
//@AutoConfigureTestEntityManager
@EntityScan(basePackages = {"io.github.transfusion.deployapp.storagemanagementservice.db.entities"})
@EnableJpaRepositories(basePackages = {"io.github.transfusion.deployapp.storagemanagementservice.db.repositories"})
@ComponentScan(basePackages = {"io.github.transfusion.deployapp.storagemanagementservice.services.storage",
        "io.github.transfusion.deployapp.storagemanagementservice.services.assets",
        "io.github.transfusion.deployapp.storagemanagementservice.mappers"})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@Import({GraalPolyglotConfig.class, Jackson2ObjectMapperBuilder.class,
        StorageCredentialMapperImpl.class,
        StorageCredsUpdateService.class,
        StorageService.class,
        AppBinaryService.class,

        SessionData.class,

        AppBinaryServiceInternalIntegrationTest.TestConfig.class
})
public class AppBinaryServiceInternalIntegrationTest {

    //    @TestConfiguration
    public static class TestConfig {
        @Bean
        @Qualifier("MainServiceWebClient")
        @Primary
        public WebClient mainServiceWebClient() {
            return Mockito.mock(WebClient.class);
        }

        @Bean
        @Primary
        public AppBinaryJobService appBinaryJobService() {
            return Mockito.mock(AppBinaryJobService.class);
        }
    }

    @Autowired
    private StorageCredentialRepository storageCredentialRepository;

    @Autowired
    private SessionData sessionData;

    @Autowired
    private AppBinaryService appBinaryService;

    @Test
    @WithAnonymousUser
    public void detectAndStoreOwnBinaryIPAAnonymousTest() throws Exception {
        Instant now = Instant.now();
        UUID storageCredentialId = UUID.randomUUID();

        MockCredential mockCredential = new MockCredential();
        mockCredential.setId(storageCredentialId);
        mockCredential.setCreatedOn(now);
        mockCredential.setCheckedOn(now);

        mockCredential.setName("mock credential");
        mockCredential.setUserId(Constants.ANONYMOUS_UID);
        mockCredential = storageCredentialRepository.save(mockCredential);
        storageCredentialId = mockCredential.getId();

        String resourceName = "apps/NineAnimator_1.2.7_1672916973.ipa";
        String absolutePath = getResourcesAbsolutePath(resourceName);
        File file = new File(absolutePath);

        AppBinary binary = appBinaryService.detectAndStoreOwnBinary(storageCredentialId, now, file);

        // now check that it is indeed in the sessionData
        Assertions.assertTrue(sessionData.getAnonymousAppBinaries().contains(binary.getId()));

        // and check that it is retrievable
        AppBinaryFilterSpecification specification =
                new AppBinaryFilterSpecification(new AppBinaryFilterCriteria("name", "like", binary.getName()));

        Page<AppBinary> page = appBinaryService.findOwnPaginatedAnonymous(specification, PageRequest.of(0, 100));
        assertThat(binary.getId(), isIn(page.stream().map(AppBinary::getId).collect(Collectors.toList())));
    }

    @Test
    @WithAnonymousUser
    public void detectAndStoreOwnBinaryAPKAnonymousTest() throws Exception {
        Instant now = Instant.now();
        UUID storageCredentialId = UUID.randomUUID();

        MockCredential mockCredential = new MockCredential();
        mockCredential.setId(storageCredentialId);
        mockCredential.setCreatedOn(now);
        mockCredential.setCheckedOn(now);

        mockCredential.setName("mock credential");
        mockCredential.setUserId(Constants.ANONYMOUS_UID);
        mockCredential = storageCredentialRepository.save(mockCredential);
        storageCredentialId = mockCredential.getId();

        String resourceName = "apps/NineAnimator_1.2.7_1672916973.ipa";
        String absolutePath = getResourcesAbsolutePath(resourceName);
        File file = new File(absolutePath);
        appBinaryService.detectAndStoreOwnBinary(storageCredentialId, now, file);

        resourceName = "apps/org.gnucash.android_24003_apps.evozi.com.apk";
        absolutePath = getResourcesAbsolutePath(resourceName);
        file = new File(absolutePath);

        AppBinary binary = appBinaryService.detectAndStoreOwnBinary(storageCredentialId, now, file);

        assertEquals(2, sessionData.getAnonymousAppBinaries().size());

        // now check that it is indeed in the sessionData
        Assertions.assertTrue(sessionData.getAnonymousAppBinaries().contains(binary.getId()));

        // and check that it is retrievable
        AppBinaryFilterSpecification specification =
                new AppBinaryFilterSpecification(new AppBinaryFilterCriteria("name", "like", binary.getName()));

        Page<AppBinary> page = appBinaryService.findOwnPaginatedAnonymous(specification, PageRequest.of(0, 100));
        assertThat(binary.getId(), isIn(page.stream().map(AppBinary::getId).collect(Collectors.toList())));
    }

    @Test
    @WithMockCustomUser(id = "80ea8267-8472-49fe-8356-8cb075b2f565")
    public void detectAndStoreBinaryIPAAuthenticatedTest() throws Exception {
        UUID userId = UUID.fromString("80ea8267-8472-49fe-8356-8cb075b2f565");

        Instant now = Instant.now();
        UUID storageCredentialId = UUID.randomUUID();

        MockCredential mockCredential = new MockCredential();
        mockCredential.setId(storageCredentialId);
        mockCredential.setCreatedOn(now);
        mockCredential.setCheckedOn(now);

        mockCredential.setName("mock credential");
        mockCredential.setUserId(userId);
        mockCredential = storageCredentialRepository.save(mockCredential);
        storageCredentialId = mockCredential.getId();

        String resourceName = "apps/NineAnimator_1.2.7_1672916973.ipa";
        String absolutePath = getResourcesAbsolutePath(resourceName);
        File file = new File(absolutePath);

        AppBinary binary = appBinaryService.detectAndStoreOwnBinary(storageCredentialId, now, file);

        // and check that it is retrievable
        AppBinaryFilterSpecification specification =
                new AppBinaryFilterSpecification(new AppBinaryFilterCriteria("name", "like", binary.getName()));

        Page<AppBinary> page = appBinaryService.findOwnPaginated(specification, PageRequest.of(0, 100));
        assertThat(binary.getId(), isIn(page.stream().map(AppBinary::getId).collect(Collectors.toList())));
    }

    @Test
    @WithMockCustomUser(id = "1b60870b-8b2a-4a66-bd31-6be99c6a6637")
    public void detectAndStoreOwnBinaryAPKAuthenticatedTest() throws Exception {
        UUID userId = UUID.fromString("1b60870b-8b2a-4a66-bd31-6be99c6a6637");

        Instant now = Instant.now();
        UUID storageCredentialId = UUID.randomUUID();

        MockCredential mockCredential = new MockCredential();
        mockCredential.setId(storageCredentialId);
        mockCredential.setCreatedOn(now);
        mockCredential.setCheckedOn(now);

        mockCredential.setName("mock credential");
        mockCredential.setUserId(userId);
        mockCredential = storageCredentialRepository.save(mockCredential);
        storageCredentialId = mockCredential.getId();

        String resourceName = "apps/NineAnimator_1.2.7_1672916973.ipa";
        String absolutePath = getResourcesAbsolutePath(resourceName);
        File file = new File(absolutePath);

        appBinaryService.detectAndStoreOwnBinary(storageCredentialId, now, file);

        resourceName = "apps/org.gnucash.android_24003_apps.evozi.com.apk";
        absolutePath = getResourcesAbsolutePath(resourceName);
        file = new File(absolutePath);

        AppBinary binary = appBinaryService.detectAndStoreOwnBinary(storageCredentialId, now, file);

        // and check that it is retrievable
        AppBinaryFilterSpecification specification =
                new AppBinaryFilterSpecification(new AppBinaryFilterCriteria("name", "like", binary.getName()));

        Page<AppBinary> page = appBinaryService.findOwnPaginated(specification, PageRequest.of(0, 100));
        assertThat(binary.getId(), isIn(page.stream().map(AppBinary::getId).collect(Collectors.toList())));

        page = appBinaryService.findOwnPaginated(null, PageRequest.of(0, 100));
        assertEquals(2, page.getTotalElements());
    }
}
