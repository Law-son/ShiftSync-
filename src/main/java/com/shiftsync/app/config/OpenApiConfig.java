package com.shiftsync.app.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI shiftSyncOpenAPI() {
        return new OpenAPI()
                .info(new Info().title("ShiftSync API")
                        .description("RESTful API backend for the ShiftSync workforce scheduling platform")
                        .version("1.0.0"));
    }
}
