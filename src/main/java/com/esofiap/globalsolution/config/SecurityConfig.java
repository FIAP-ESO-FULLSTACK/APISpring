package com.esofiap.globalsolution.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Configuração de segurança que define o PasswordEncoder e as regras de autorização HTTP
 * para usar autenticação baseada em cookies.
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
                .authorizeHttpRequests(auth -> auth
                        // Endpoints públicos
                        .requestMatchers("/api/users/register", "/api/status/**", "/api/metadata/**").permitAll()
                        // Qualquer outro request exige autenticação
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        // Define o endpoint que processará o login (POST para /login)
                        .loginProcessingUrl("/login")
                        // Define a URL para qual redirecionar em caso de sucesso
                        .defaultSuccessUrl("/api/status/db-teste", true)
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
}