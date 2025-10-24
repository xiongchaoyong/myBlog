package com.x.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("博客前台接口文档")
                        .version("1.0.0")
                        .description("博客前台")
                        .contact(new Contact()
                                .name("X")
                                .url("https://github.com/XIONGCHAOYONG/myBlog")
                                .email("1846326845@qq.com"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0")));
    }
//    http://localhost:8080/doc.html
}