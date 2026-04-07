package com.exe.vocafy_BE.config

import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.security.SecurityRequirement
import io.swagger.v3.oas.models.security.SecurityScheme
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import io.swagger.v3.oas.models.servers.Server


@Configuration
class OpenApiConfig {

    @Bean
    fun openApi(): OpenAPI {
        val bearerSchemeName = "bearerAuth"
        return OpenAPI()
            .info(
                Info()
                    .title("Vocafy API")
                    .version("v1")
            )
            .addSecurityItem(SecurityRequirement().addList(bearerSchemeName))
            .components(
                io.swagger.v3.oas.models.Components()
                    .addSecuritySchemes(
                        bearerSchemeName,
                        SecurityScheme()
                            .name(bearerSchemeName)
                            .type(SecurityScheme.Type.HTTP)
                            .scheme("bearer")
                            .bearerFormat("JWT")
                    )
            )
                    // Chạy local thì phong ấn cái này lại.
            .servers(
                listOf(
                    Server().url("https://vocafy.milize-lena.space")
                        .description("Production server")
                )
            )
    }


}
