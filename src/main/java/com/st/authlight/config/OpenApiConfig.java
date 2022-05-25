package com.st.authlight.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

import static io.swagger.v3.oas.models.security.SecurityScheme.Type.HTTP;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenApi() {
        var securitySchemeName = "bearerAuth";
        var requirements = List.of(new SecurityRequirement().addList(securitySchemeName));

        return new OpenAPI().components(new Components().addSecuritySchemes(securitySchemeName,
                        new SecurityScheme().type(HTTP).scheme("bearer").bearerFormat("JWT")))
                .security(requirements);
    }
}