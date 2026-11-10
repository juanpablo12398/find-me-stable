package edu.utn.proyecto.infrastructure.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Configuración global de la documentación OpenAPI (Swagger).
 * UI disponible en: /swagger-ui.html
 * Spec JSON en:     /v3/api-docs
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI findMeOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Find Me API")
                        .description("""
                                API REST del sistema Find Me — plataforma académica para el registro,
                                búsqueda y visualización de avistamientos georreferenciados de personas
                                desaparecidas.

                                **Autenticación:** JWT almacenado en cookie HttpOnly `FM_TOKEN`.
                                Los endpoints marcados con 🔒 requieren sesión activa.

                                ⚠️ La validación de identidad usa RENAPER simulado (ver README).
                                """)
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Universidad Tecnológica Nacional")
                                .email("proyecto@utn.edu.ar"))
                        .license(new License()
                                .name("Uso académico")
                                .url("https://utn.edu.ar")))
                .servers(List.of(
                        new Server().url("http://localhost:8080").description("Desarrollo local"),
                        new Server().url("https://api.findme.app").description("Producción")
                ));
    }
}
