package io.github.transfusion.deployapp.storagemanagementservice.db;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.transfusion.deployapp.storagemanagementservice.db.entities.AppBinary;
import io.github.transfusion.deployapp.storagemanagementservice.db.entities.Ipa;
import io.github.transfusion.deployapp.storagemanagementservice.db.repositories.AppBinaryRepository;
import io.github.transfusion.deployapp.storagemanagementservice.db.specifications.AppBinaryFilterCriteria;
import io.github.transfusion.deployapp.storagemanagementservice.db.specifications.AppBinaryFilterSpecification;
import org.jobrunr.scheduling.JobScheduler;
import org.jobrunr.storage.StorageProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.collection.IsIn.isIn;

//@RunWith(SpringJUnit4ClassRunner.class)
@ActiveProfiles({"db-test"})
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith({SpringExtension.class, MockitoExtension.class})
@DataJpaTest
@Import({Jackson2ObjectMapperBuilder.class})
//@Transactional
//@TransactionConfiguration
//@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class AppBinaryFilterSpecificationTest {

    @TestConfiguration
    public static class TestConfig {
        @Bean
//        @Qualifier("MainServiceWebClient")
        @Primary
        public JobScheduler jobScheduler() {
            return Mockito.mock(JobScheduler.class);
        }

        @Bean
        @Primary
        public StorageProvider storageProvider() {
            return Mockito.mock(StorageProvider.class);
        }
    }

    @Autowired
    private AppBinaryRepository repository;

    private Ipa tmp;
    private Ipa foo;
    private Ipa bar;

    @BeforeEach
    public void setup() {
        ObjectMapper mapper = new ObjectMapper();

        tmp = new Ipa();
        tmp.setId(UUID.randomUUID());
        tmp.setVersion("foo");
        tmp.setBuild("bar");
        tmp.setUploadDate(Instant.now());

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
        bar = repository.save(bar);
    }

    @Test
    public void likeNameSpecification() {
        AppBinaryFilterSpecification specification =
                new AppBinaryFilterSpecification(new AppBinaryFilterCriteria("name", "like", "foo_"));
        List<AppBinary> results = repository.findAll(specification);
        assertThat(foo, isIn(results));
        assertThat(bar, not(isIn(results)));

        specification =
                new AppBinaryFilterSpecification(new AppBinaryFilterCriteria("name", "like", "bar_"));
        results = repository.findAll(specification);
        assertThat(foo, not(isIn(results)));
        assertThat(bar, isIn(results));
    }
}
