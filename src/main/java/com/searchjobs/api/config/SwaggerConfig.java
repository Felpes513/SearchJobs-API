package com.searchjobs.api.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {

    private static final String SECURITY_SCHEME_NAME = "bearerAuth";

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Search Jobs API")
                        .description("""
                                API REST para a plataforma Search Jobs.

                                ## Funcionalidades
                                - **Autenticação** via JWT (access token + refresh token)
                                - **Upload de currículos** em formato PDF (máx. 10 MB)
                                - **Extração inteligente** de dados do currículo via OpenAI GPT

                                ## Autenticação
                                Endpoints protegidos exigem o header `Authorization: Bearer <access_token>`.
                                Obtenha o token através do endpoint `POST /api/auth/login`.
                                """)
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Search Jobs")
                                .email("contato@searchjobs.com")))
                .servers(List.of(
                        new Server().url("http://localhost:8080").description("Ambiente local")
                ))
                .addSecurityItem(new SecurityRequirement().addList(SECURITY_SCHEME_NAME))
                .components(new Components()
                        .addSecuritySchemes(SECURITY_SCHEME_NAME, new SecurityScheme()
                                .name(SECURITY_SCHEME_NAME)
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("Informe o access token JWT obtido no login. Exemplo: `Bearer eyJhbGci...`")));
    }
}