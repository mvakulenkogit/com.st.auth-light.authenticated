package com.st.authlight.config;

import com.st.authlight.config.jwt.AuthenticationManager;
import com.st.authlight.config.jwt.SecurityContextRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

import java.util.List;

import static org.springframework.web.cors.CorsConfiguration.ALL;

@Configuration
@RequiredArgsConstructor
@EnableReactiveMethodSecurity
public class SpringConfig {

    @Bean
    public SecurityWebFilterChain springWebFilterChain(ServerHttpSecurity http,
                                                       AuthenticationManager authenticationManager,
                                                       SecurityContextRepository securityContextRepository) {
        return http.csrf().disable()
                .cors().configurationSource(corsSource()).and()
                .httpBasic().disable()
                .authenticationManager(authenticationManager)
                .securityContextRepository(securityContextRepository)
                .authorizeExchange().anyExchange().permitAll()
                .and().build();
    }

    private UrlBasedCorsConfigurationSource corsSource() {
        var corsConfig = new CorsConfiguration();
        corsConfig.setAllowedOrigins(List.of(ALL));
        corsConfig.setAllowedMethods(List.of(ALL));
        corsConfig.setAllowedHeaders(List.of(ALL));
        corsConfig.setMaxAge(60L);

        var source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfig);
        return source;
    }
}