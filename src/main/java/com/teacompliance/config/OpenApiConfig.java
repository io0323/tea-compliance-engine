package com.teacompliance.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * OpenAPI設定クラス
 */
@Configuration
public class OpenApiConfig {
    
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("TeaCompliance Engine API")
                .description("茶葉ロットのコンプライアンス評価を自動化する業務向けエンジンAPI")
                .version("1.0.0")
                .contact(new Contact()
                    .name("TeaCompliance Team")
                    .email("support@teacompliance.com"))
                .license(new License()
                    .name("MIT License")
                    .url("https://opensource.org/licenses/MIT")))
            .servers(List.of(
                new Server()
                    .url("http://localhost:8080")
                    .description("開発環境"),
                new Server()
                    .url("https://api.teacompliance.com")
                    .description("本番環境")
            ));
    }
}
