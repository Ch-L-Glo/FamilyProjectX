package com.familyprojectx.finance.common.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI familyFinanceOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("FamilyProjectX Phase 1 API")
                        .version("0.0.1")
                        .description("Authentication, family, transaction, split, settlement, balance, category, and budget APIs."));
    }
}
