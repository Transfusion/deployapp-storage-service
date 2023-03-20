package io.github.transfusion.deployapp.storagemanagementservice.config;

import org.graalvm.polyglot.Engine;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

@Configuration
public class GraalPolyglotConfig {

    // TODO: stress test this for memory leaks...
    /**
     * https://github.com/oracle/graaljs/issues/561
     * https://github.com/oracle/graaljs/issues/481
     * "To reduce memory consumption, you can share a single Engine across multiple independent Context instances"
     * https://www.graalvm.org/22.3/reference-manual/embed-languages/#code-caching-across-multiple-contexts
     **/
    @Bean
    @Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
    Engine polyglotEngine() {
        return Engine.newBuilder().build();
    }

}
