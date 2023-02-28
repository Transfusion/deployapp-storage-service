package io.github.transfusion.deployapp.storagemanagementservice.controller;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@Profile({"!db-test"})
public class WebSecurityConfig {
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.cors(Customizer.withDefaults())
//                .and()
                .csrf()
                .disable().
                authorizeRequests()
                .antMatchers("/api/**",
                        "/oauth2/**", "/api-docs/**",
                        "/api/*/user/profile", "/api/*/credentials/**", "/api/v1/app/binary", "/api/v1/app/alias",
                        "/api/*/utility/public/**", "/microservice-api/**", "/actuator/**")
                .permitAll();
        return http.build();
    }
}
