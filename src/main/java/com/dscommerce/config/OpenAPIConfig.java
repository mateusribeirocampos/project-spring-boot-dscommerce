package com.dscommerce.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenAPIConfig {

    @Value("${security.client-id}")
    private String clientId;

    @Value("${security.client-secret}")
    private String clientSecret;

    @Value("${mock.client-email}")
    private String clientEmail;

    @Value("${mock.client-password}")
    private String clientPassword;

    @Bean
    public OpenAPI dscommerceAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Dscommerce (E-commerce) API")
                        .description("API REST for e-commerce. OAuth2 authentication with JWT.")
                        .version("v1.0.4")
                        .license(new License().name("MIT")
                                .url("https://github.com/mateusribeirocampos/project-spring-boot-dscommerce")))
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"))
                .components(new Components()
                        .addSecuritySchemes("bearerAuth", new SecurityScheme()
                                .name("bearerAuth")
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")))
                .path("/oauth2/token", new PathItem()
                        .post(new Operation()
                                .tags(List.of("Authentication"))
                                .summary("Get / Refresh Access Token")
                                .security(List.of())
                                .description("Supports two grant types: `password` (login) and `refresh_token` (token rotation).")
                                .requestBody(new io.swagger.v3.oas.models.parameters.RequestBody()
                                        .content(new Content().addMediaType(
                                                "application/x-www-form-urlencoded",
                                                new MediaType().schema(new Schema<String>()
                                                        .oneOf(List.of(
                                                                new Schema<String>()
                                                                        .type("object")
                                                                        .description("Password grant (login)")
                                                                        .addProperty("grant_type",
                                                                                new Schema<String>().type("string")
                                                                                        ._enum(List.of("password")))
                                                                        .addProperty("client_id",
                                                                                new Schema<>().type("string")
                                                                                        .example(clientId))
                                                                        .addProperty("client_secret",
                                                                                new Schema<>().type("string")
                                                                                        .example(clientSecret))
                                                                        .addProperty("username",
                                                                                new Schema<>().type("string")
                                                                                        .example(clientEmail))
                                                                        .addProperty("password",
                                                                                new Schema<>().type("string")
                                                                                        .example(clientPassword))
                                                                        .required(List.of("grant_type", "client_id", "client_secret", "username", "password")),
                                                                new Schema<String>()
                                                                        .type("object")
                                                                        .description("Refresh token grant (rotation)")
                                                                        .addProperty("grant_type",
                                                                                new Schema<>().type("string")._enum(List.of("refresh_token")))
                                                                        .addProperty("client_id",
                                                                                new Schema<>().type("string").example(clientId))
                                                                        .addProperty("client_secret",
                                                                                new Schema<>().type("string").example(clientSecret))
                                                                        .addProperty("refresh_token",
                                                                                new Schema<>().type("string").example("<refresh_token_value>"))
                                                                        .required(List.of("grant_type", "client_id", "client_secret", "refresh_token"))))))))
                                .responses(new ApiResponses()
                                        .addApiResponse("200", new ApiResponse()
                                                .description("OK")
                                                .content(new Content().addMediaType("application/json",
                                                        new MediaType().schema(new Schema<>().type("object")
                                                                .addProperty("access_token", new Schema<>()
                                                                        .type("string"))
                                                                .addProperty("refresh_token", new Schema<>()
                                                                        .type("string"))
                                                                .addProperty("token_type",
                                                                        new Schema<>().type("string")
                                                                                .example("Bearer"))
                                                                .addProperty("expires_in", new Schema<>()
                                                                        .type("integer"))))))
                                        .addApiResponse("401", new ApiResponse()
                                                .description("Invalid credentials")))));
    }
}
