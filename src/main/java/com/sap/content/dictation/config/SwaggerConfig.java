package com.sap.content.dictation.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * @author : Jenson.Liu
 * @date : 2020/5/27  3:44 下午
 */
@Configuration
@EnableSwagger2
public class SwaggerConfig {
    @Bean
    public Docket createRestApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .pathMapping("/")
                .select()
                /**
                 * 需要配置扫描的controller包
                 */
                .apis(RequestHandlerSelectors.basePackage("com.sap.content.dictation.apicontroller"))
                .paths(PathSelectors.any())
                .build().apiInfo(new ApiInfoBuilder()
                        .title("关于文字图像识别API说明")
                        .contact(new Contact("Jenson","https://blog.csdn.net/coderping","jenson.liu@sap.com"))
                        .build());
    }
}

