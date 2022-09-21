package io.github.transfusion.deployapp.storagemanagementservice.config;

import org.graalvm.polyglot.Context;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

@Configuration
public class GraalPolyglotConfig {

    // TODO: stress test this for memory leaks...
    @Bean
    @Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
    Context polyglotContext() {
        Context ctx = Context.newBuilder().allowAllAccess(true).build();
        ctx.eval("ruby", "Encoding.default_external = 'ISO-8859-1'");
        ctx.eval("ruby", "require 'app-info'");
        ctx.eval("ruby", "require 'json'");
        return ctx;
    }

}
