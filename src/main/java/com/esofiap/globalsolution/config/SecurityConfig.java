package com.esofiap.globalsolution.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import jakarta.servlet.http.HttpServletResponse;

// --- NOVOS IMPORTS PARA O CORS ---
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import java.util.Arrays;
import static org.springframework.security.config.Customizer.withDefaults;
// --- FIM DOS NOVOS IMPORTS ---

/**
 * Configuração de segurança que define o PasswordEncoder e as regras de autorização HTTP
 * para usar autenticação baseada em cookies.
 *
 * AGORA TAMBÉM CONFIGURA O CORS para permitir o React Native.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http.csrf(AbstractHttpConfigurer::disable)
            // --- MUDANÇA 1: HABILITA A CONFIGURAÇÃO DE CORS ABAIXO ---
            .cors(withDefaults()) 
            .authorizeHttpRequests(auth -> auth
                    // Endpoints públicos
                    .requestMatchers("/api/users/register", "/api/status/**", "/api/metadata/**").permitAll()
                    // Qualquer outro request exige autenticação
                    .anyRequest().authenticated()
            )
            .formLogin(form -> form
                    // Define o endpoint que processará o login (POST para /login)
                    .loginProcessingUrl("/login")
                    // Evita redirect para que o fetch enxergue o Set-Cookie
                    .successHandler((request, response, authentication) -> {
                        response.setStatus(HttpServletResponse.SC_OK);
                        response.setContentType("application/json");
                        response.getWriter().write("{\"message\":\"Login ok\"}");
                        response.getWriter().flush();
                    })
                    .failureHandler((request, response, exception) -> {
                        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                        response.setContentType("application/json");
                        response.getWriter().write("{\"message\":\"Credenciais inválidas\"}");
                        response.getWriter().flush();
                    })
                    // Permite que todos acessem a página/endpoint de login
                    .permitAll()
            )
            .logout(logout -> logout
                    // Define o endpoint para logout (POST para /logout)
                    .logoutUrl("/logout")
                    // Invalida a sessão HTTP e limpa o cookie
                    .invalidateHttpSession(true)
                    .deleteCookies("JSESSIONID")
                    .permitAll()
            )
            .sessionManagement(session -> session
                    // Indica que o Spring gerenciará a sessão HTTP (via cookie JSESSIONID)
                    .sessionCreationPolicy(org.springframework.security.config.http.SessionCreationPolicy.IF_REQUIRED)
            );

        return http.build();
    }

    // --- MUDANÇA 2: NOVO MÉTODO PARA CONFIGURAR O CORS ---

    /**
     * Configura o CORS (Cross-Origin Resource Sharing) para permitir que o
     * front-end React Native (rodando em localhost:8081, 19000, etc.)
     * se comunique com este backend.
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        
        // Permite origens do React Native (iOS, Android, Web)
        configuration.setAllowedOrigins(Arrays.asList(
            "http://localhost:8081", // Porta padrão do Metro
            "http://localhost:19000", // Porta comum do Expo
            "http://localhost:19001", // Outra porta comum
            "http://10.0.2.2:8081"   // Para caso um dia use Android
        ));
        
        // Métodos que o app pode usar
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        
        // Permite todos os cabeçalhos (como Content-Type, Authorization, etc.)
        configuration.setAllowedHeaders(Arrays.asList("*"));
        
        // A LINHA MAIS IMPORTANTE:
        // Permite que o app envie e receba credenciais (o cookie JSESSIONID)
        configuration.setAllowCredentials(true); 
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration); // Aplica essa regra para toda a API
        return source;
    }
}
