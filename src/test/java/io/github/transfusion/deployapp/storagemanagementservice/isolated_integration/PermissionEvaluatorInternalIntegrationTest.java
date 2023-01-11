package io.github.transfusion.deployapp.storagemanagementservice.isolated_integration;

import io.github.transfusion.deployapp.auth.CustomUserPrincipal;
import io.github.transfusion.deployapp.session.SessionData;
import io.github.transfusion.deployapp.storagemanagementservice.WithMockCustomUser;
import io.github.transfusion.deployapp.storagemanagementservice.auth.CustomGlobalMethodSecurityConfiguration;
import io.github.transfusion.deployapp.storagemanagementservice.auth.CustomPermissionEvaluator;
import io.github.transfusion.deployapp.storagemanagementservice.config.GraalPolyglotConfig;
import io.github.transfusion.deployapp.storagemanagementservice.controller.AppController;
import io.github.transfusion.deployapp.storagemanagementservice.controller.PublicUtilityController;
import io.github.transfusion.deployapp.storagemanagementservice.controller.WebSecurityConfig;
import io.github.transfusion.deployapp.storagemanagementservice.db.entities.AppBinary;
import io.github.transfusion.deployapp.storagemanagementservice.db.entities.MockCredential;
import io.github.transfusion.deployapp.storagemanagementservice.db.repositories.AppBinaryRepository;
import io.github.transfusion.deployapp.storagemanagementservice.db.repositories.StorageCredentialRepository;
import io.github.transfusion.deployapp.storagemanagementservice.mappers.StorageCredentialMapperImpl;
import io.github.transfusion.deployapp.storagemanagementservice.services.*;
import org.junit.jupiter.api.BeforeEach;
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
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.File;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static io.github.transfusion.deployapp.storagemanagementservice.Utilities.getResourcesAbsolutePath;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;

//@ActiveProfiles("db-test")
@ExtendWith({SpringExtension.class, /*MockitoExtension.class*/})
//@DataJpaTest
@AutoConfigureMockMvc
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
@Import({WebSecurityConfig.class,
        GraalPolyglotConfig.class, Jackson2ObjectMapperBuilder.class,
        StorageCredentialMapperImpl.class,
        StorageCredsUpdateService.class,
        StorageService.class,
        AppBinaryService.class,

        SessionData.class,

        // important!
        CustomGlobalMethodSecurityConfiguration.class,
        CustomPermissionEvaluator.class,

//        controllers
        PublicUtilityController.class,
        AppController.class,

        PermissionEvaluatorInternalIntegrationTest.TestConfig.class
})
public class PermissionEvaluatorInternalIntegrationTest {

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

        @Bean
        @Primary
        public AppBinaryDownloadsService appBinaryDownloadsService() {
            return Mockito.mock(AppBinaryDownloadsService.class);
        }
    }

    @Autowired
    private StorageCredentialRepository storageCredentialRepository;

    @Autowired
    private AppBinaryService appBinaryService;

    @Autowired
    private AppBinaryRepository appBinaryRepository;

    private final static String MOCK_USER_ID = "586a7200-20d9-42df-aafe-8ecdb44323de";
    private static boolean setUpIsDone = false;

    private static UUID binaryId = null;

    @BeforeEach
    public void setUp() throws Exception {
        // https://stackoverflow.com/questions/12087959/junit-run-set-up-method-once
        if (setUpIsDone) return;

        Authentication backupAuth = SecurityContextHolder.getContext().getAuthentication(); // backup

        Instant now = Instant.now();
        UUID storageCredentialId = UUID.randomUUID();

        MockCredential mockCredential = new MockCredential();
        mockCredential.setId(storageCredentialId);
        mockCredential.setCreatedOn(now);
        mockCredential.setCheckedOn(now);

        mockCredential.setName("mock credential");
        mockCredential.setUserId(UUID.fromString(MOCK_USER_ID));
        mockCredential = storageCredentialRepository.save(mockCredential);
        storageCredentialId = mockCredential.getId();

        String resourceName = "apps/NineAnimator_1.2.7_1672916973.ipa";
        String absolutePath = getResourcesAbsolutePath(resourceName);
        File file = new File(absolutePath);

        // simulate login
        List<GrantedAuthority> grantedAuthorities = Arrays.stream(new String[]{"ROLE_USER"}).map(a -> (GrantedAuthority) () -> a).collect(Collectors.toList());
        CustomUserPrincipal principal =
                new CustomUserPrincipal(UUID.fromString(MOCK_USER_ID),
                        "foobar", "foo@bar.com",
                        "foobar", "Foo Bar",
                        true,
                        grantedAuthorities);
        Authentication auth =
                new OAuth2AuthenticationToken(principal, principal.getAuthorities(),
                        "google");
        SecurityContextHolder.getContext().setAuthentication(auth);
        // logged in!

        AppBinary binary = appBinaryService.detectAndStoreOwnBinary(storageCredentialId, now, file);

        binaryId = binary.getId();

        SecurityContextHolder.getContext().setAuthentication(backupAuth); // restore context

        setUpIsDone = true;
    }

    @Autowired
    private WebApplicationContext context;

    private MockMvc mvc;

    @BeforeEach
    public void setup() {
        mvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    @Test
    @WithAnonymousUser
    public void testRequest() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/api/v1/utility/public/version"))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }


    @Test
    @WithMockCustomUser(id = MOCK_USER_ID)
    public void getAppBinaryByIdAuthenticatedTest() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get(String.format("/api/v1/app/binary/%s", binaryId.toString())))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    @WithAnonymousUser
    public void getAppBinaryByIdAnonymousTest() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get(String.format("/api/v1/app/binary/%s", binaryId.toString())))
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }
}
