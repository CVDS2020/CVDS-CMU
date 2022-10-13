package com.css.cvds.cmu.conf;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springdoc.core.GroupedOpenApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author lin
 */
@Configuration
public class SpringDocConfig {

    @Value("${doc.enabled: true}")
    private boolean enable;

    @Bean
    public OpenAPI springShopOpenApi() {
        Contact contact = new Contact();
        contact.setName("css");
        contact.setEmail("css@qq.com");
        return new OpenAPI()
                .info(new Info().title("CVDS 接口文档")
                        .contact(contact)
                        .description("CVDS 视频平台")
                        .version("v2.0")
                        .license(new License().name("Apache 2.0").url("http://springdoc.org")));
    }

    /**
     * 添加分组
     * @return
     */
    @Bean
    public GroupedOpenApi publicApi() {
        return GroupedOpenApi.builder()
                .group("1. 全部")
                .packagesToScan("com.css.cvds.cmu.vmanager")
                .build();
    }

    @Bean
    public GroupedOpenApi publicApi2() {
        return GroupedOpenApi.builder()
                .group("2. 国标28181")
                .packagesToScan("com.css.cvds.cmu.vmanager.gb28181")
                .build();
    }

    @Bean
    public GroupedOpenApi publicApi5() {
        return GroupedOpenApi.builder()
                .group("4. 服务管理")
                .packagesToScan("com.css.cvds.cmu.vmanager.server")
                .build();
    }

    @Bean
    public GroupedOpenApi publicApi6() {
        return GroupedOpenApi.builder()
                .group("5. 用户管理")
                .packagesToScan("com.css.cvds.cmu.vmanager.user")
                .build();
    }
}
