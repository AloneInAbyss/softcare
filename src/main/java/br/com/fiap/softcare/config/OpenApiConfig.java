package br.com.fiap.softcare.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * OpenAPI 3 (Swagger) configuration for SoftCare API documentation
 */
@Configuration
public class OpenApiConfig {

    @Value("${server.port:8080}")
    private String serverPort;

    @Value("${server.servlet.context-path:/api/v1}")
    private String contextPath;

    @Bean
    public OpenAPI softCareOpenAPI() {
        return new OpenAPI()
                .info(apiInfo())
                .servers(List.of(
                        new Server()
                                .url("http://localhost:" + serverPort + contextPath)
                                .description("Local Development Server")
                ))
                .addSecurityItem(new SecurityRequirement().addList("HTTP Basic Authentication"))
                .components(new Components()
                        .addSecuritySchemes("HTTP Basic Authentication",
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("basic")
                                        .description("HTTP Basic Authentication. Use: admin / admin123")
                        )
                );
    }

    private Info apiInfo() {
        return new Info()
                .title("🌟 SoftCare API")
                .description("""
                        ## SoftCare - Plataforma de Bem-Estar Corporativo
                        
                        **SoftCare** é uma solução completa para oferecer suporte psicossocial e promover o bem-estar emocional dos colaboradores.
                        
                        ### 🎯 Funcionalidades Principais
                        - **👤 Gestão de Usuários** - CRUD completo com autenticação segura
                        - **📞 Canais de Suporte** - Cadastro e busca de canais de apoio
                        - **📔 Diário Emocional** - Registro e análise de humor diário
                        - **🧠 Avaliações Psicossociais** - Questionários e cálculo de riscos
                        - **📊 Logs de Auditoria** - Rastreamento completo de operações
                        
                        ### 🔐 Autenticação
                        Todos os endpoints (exceto alguns públicos) requerem **HTTP Basic Authentication**:
                        - **Usuário:** `admin`
                        - **Senha:** `admin123`
                        
                        ### 🛡️ Segurança
                        - **BCrypt** para criptografia de senhas
                        - **Jakarta Validation** para validação robusta
                        - **Audit Logging** para rastreamento de segurança
                        - **Spring Security** para controle de acesso
                        
                        ### 📊 Tecnologias
                        - **Spring Boot 3.5.6** - Framework principal
                        - **MongoDB 7.0** - Banco de dados NoSQL
                        - **Spring Security** - Autenticação e autorização
                        - **Java 17** - Plataforma de execução
                        """)
                .version("1.0.0");
    }
}