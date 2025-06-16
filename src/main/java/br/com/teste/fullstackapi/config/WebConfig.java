package br.com.teste.fullstackapi.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @SuppressWarnings("null")
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**") // Aplica a política de CORS apenas aos endpoints da API
            .allowedOrigins("http://localhost:4200") // Permite requisições da sua aplicação Angular local
            // .allowedOrigins("https://seu-dominio-de-frontend.com") // Em produção, você mudaria para o domínio real
            .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // Métodos HTTP permitidos
            .allowedHeaders("*") // Permite todos os cabeçalhos
            .allowCredentials(true); // Permite o envio de credenciais (cookies, etc., embora não usemos para JWT)
    }
}