package com.example.jarvis.config;
import com.example.jarvis.entity.User;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.*;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.parameters.Parameter;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.web.method.HandlerMethod;



@Configuration
@OpenAPIDefinition(info = @Info(title = "Jarvis Project",
        description = "This project include docker, keycloak, liquibase, swagger, spring boot, postgres",
        contact = @Contact(name = "OM KATE",email = "officialomkate@gmail.com",url = "https://www.linkedin.com/in/om-kate-52a376256"),
        version = "/v1",
        termsOfService = "Term and Condition Applied!"),
        security = @SecurityRequirement(name = "KeycloakAuth")
)
//@SecurityScheme(name = "KeycloakAuth",
//        openIdConnectUrl = "http://localhost:8090/realms/Hogwarts/.well-known/openid-configuration",
//        scheme = "bearer",
//        type = SecuritySchemeType.OPENIDCONNECT,
//        in = SecuritySchemeIn.HEADER
//)
@SecurityScheme(
        name = "KeycloakAuth",
        type = SecuritySchemeType.OAUTH2,
        in = SecuritySchemeIn.HEADER,
        scheme = "bearer",
        flows = @OAuthFlows(
                password = @OAuthFlow(
                        tokenUrl = "http://localhost:8090/realms/Hogwarts/protocol/openid-connect/token"
                )
        )
)
public class SwaggerConfig {



//    To Add Custom Parameter Field to all API GlobalOpenApiCustomizer is Used
    @Bean
    public OperationCustomizer customizer() {
        return (Operation operation, HandlerMethod handlerMethod) ->
        {
            Parameter parameter = new Parameter()
                    .in("header")
                    .name("Auth")
                    .description("description");
            operation.addParametersItem(parameter);
            return operation;
        };
    }

}