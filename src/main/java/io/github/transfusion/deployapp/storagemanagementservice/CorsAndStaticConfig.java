package io.github.transfusion.deployapp.storagemanagementservice;


import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import org.springframework.web.servlet.config.annotation.*;

import java.util.List;

@Configuration
@EnableWebMvc
@Profile({"!db-test"})
public class CorsAndStaticConfig {
    @Autowired
    private Environment env;

    @Value("${custom_cors.origins}")
    private List<String> corsOrigins;

    // /actuator/** CORS configuration is done in the application properties
    @Bean
    public WebMvcConfigurer corsConfigurer() {

        final String[] CLASSPATH_RESOURCE_LOCATIONS = {
                "classpath:/static/"
        };

        return new WebMvcConfigurer() {

            @Override
            public void addResourceHandlers(ResourceHandlerRegistry registry) {
                registry.addResourceHandler("/static/**")
                        .addResourceLocations(CLASSPATH_RESOURCE_LOCATIONS);
            }

            @Override
            public void addCorsMappings(@NotNull CorsRegistry registry) {
//                List<String> urls = env.getProperty("custom_cors.origins", List.class);
                CorsRegistration reg = registry.addMapping("/api/**");
                reg.allowedOrigins(corsOrigins.toArray(new String[0]));
                reg.allowedHeaders("*").allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS").allowCredentials(true);
            }
        };
    }

}
