package io.github.transfusion.deployapp.storagemanagementservice.controller;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
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
                        "/api/*/user/profile", "/api/*/credentials/**", "/api/v1/app/binary",
                        "/api/*/utility/public/**", "/microservice-api/**")
                .permitAll();
        return http.build();
    }
}
