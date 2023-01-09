package io.github.transfusion.deployapp.storagemanagementservice.db;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.transfusion.deployapp.dto.response.AppBinaryDTO;
import io.github.transfusion.deployapp.storagemanagementservice.WithMockCustomUser;
import io.github.transfusion.deployapp.storagemanagementservice.config.GraalPolyglotConfig;
import io.github.transfusion.deployapp.storagemanagementservice.db.entities.Ipa;
import io.github.transfusion.deployapp.storagemanagementservice.db.repositories.AppBinaryRepository;
import io.github.transfusion.deployapp.storagemanagementservice.mappers.AppBinaryMapper;
import io.github.transfusion.deployapp.storagemanagementservice.mappers.AppBinaryMapperImpl;
import io.github.transfusion.deployapp.storagemanagementservice.mappers.AppDetailsMapperImpl;
import io.github.transfusion.deployapp.storagemanagementservice.mappers.StorageCredentialMapperImpl;
import io.github.transfusion.deployapp.storagemanagementservice.services.AppBinaryService;
import io.github.transfusion.deployapp.storagemanagementservice.services.StorageCredsUpdateService;
import io.github.transfusion.deployapp.storagemanagementservice.services.StorageService;
import org.jobrunr.scheduling.JobScheduler;
import org.jobrunr.storage.StorageProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.reactive.function.client.WebClient;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Arrays;
import java.util.UUID;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.collection.IsIn.isIn;

@ActiveProfiles({"db-test"})
@ExtendWith({SpringExtension.class, MockitoExtension.class})
@DataJpaTest(properties = {"spring.main.allow-bean-definition-overriding=true"})
@Import({GraalPolyglotConfig.class, Jackson2ObjectMapperBuilder.class,

        StorageCredentialMapperImpl.class,
        StorageCredsUpdateService.class,
        StorageService.class,

        AppDetailsMapperImpl.class,
        AppBinaryMapperImpl.class,
        AppBinaryService.class,


        AppBinaryServiceTest.TestConfig.class})
@DirtiesContext(classMode= DirtiesContext.ClassMode.AFTER_CLASS)
public class AppBinaryServiceTest {

//    @TestConfiguration
//    https://stackoverflow.com/questions/46343450/inner-static-class-with-configuration-picked-up-by-spring-scanner-for-all-tests
    public static class TestConfig {
        @Bean
        @Primary
        public JobScheduler jobScheduler() {
            return Mockito.mock(JobScheduler.class);
        }

        @Bean
        @Qualifier("MainServiceWebClient")
        @Primary
        public WebClient mainServiceWebClient() {
            return Mockito.mock(WebClient.class);
        }

        @Bean
        @Primary
        public StorageProvider storageProvider() {
            return Mockito.mock(StorageProvider.class);
        }
    }

    @Autowired
    private AppBinaryService appBinaryService;

    @Autowired
    private AppBinaryRepository repository;

    @Autowired
    private AppBinaryMapper appBinaryMapper;

    private Ipa tmp;
    private Ipa foo;
    private Ipa bar;

    @BeforeEach
    public void setup() {
        ObjectMapper mapper = new ObjectMapper();

        tmp = new Ipa();
        tmp.setId(UUID.randomUUID());
        tmp.setStorageCredential(UUID.randomUUID());
        tmp.setVersion("foo");
        tmp.setBuild("bar");
        tmp.setUploadDate(Instant.now());

        tmp.setUserId(UUID.fromString("8ea610ed-170e-4f91-b7f0-a7480a8ba8e7"));

        tmp.setName("foo_name");
        tmp.setIdentifier("com.foo.one");

        tmp.setAssetsOnFrontPage(true);
        tmp.setSizeBytes(BigDecimal.valueOf(42));
        tmp.setFileName("foo.bar");

        tmp.setMinSdkVersion("1.0");
        tmp.setIphone(true);
        tmp.setIpad(true);
        tmp.setUniversal(true);
        tmp.setArchs(Arrays.asList("foo", "bar"));
        tmp.setPlistJson(mapper.createObjectNode());
        foo = repository.save(tmp);

        bar = tmp;
        bar.setName("bar_name");
        bar.setId(UUID.randomUUID());
        bar.setIdentifier("com.bar.one");
        bar.setUserId(UUID.fromString("3ecd1e08-421d-4550-a741-9116f3630cd2"));
        bar = repository.save(bar);
    }

    /**
     * tests whether the findOwnPaginated endpoint only returns the user's own uploaded apps
     */
    @Test
    @WithMockCustomUser(id = "8ea610ed-170e-4f91-b7f0-a7480a8ba8e7")
    public void testFindOwnPaginated() {
        // populate the securitycontext with a given uuid
        Page<AppBinaryDTO> page = appBinaryService.findOwnPaginated(null, PageRequest.of(0, 100)).map(appBinaryMapper::toDTO);

        assertThat(appBinaryMapper.toDTO(foo), isIn(page.getContent()));
        assertThat(appBinaryMapper.toDTO(bar), not(isIn(page.getContent())));
    }
}
