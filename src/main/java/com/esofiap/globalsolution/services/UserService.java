package com.esofiap.globalsolution.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.esofiap.globalsolution.dto.UserRegistrationRequest;

@Service
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);
    private final JdbcTemplate jdbcTemplate;
    private final String userSchema;
    private final PasswordEncoder passwordEncoder;

    public UserService(JdbcTemplate jdbcTemplate,
                       @Value("${app.oracle.default-schema}") String defaultSchema,
                       PasswordEncoder passwordEncoder) {
        this.jdbcTemplate = jdbcTemplate;
        this.userSchema = defaultSchema.toUpperCase();
        this.passwordEncoder = passwordEncoder;
    }

    public int registerNewUser(UserRegistrationRequest request) {
        String encodedPassword = passwordEncoder.encode(request.senha());

        // Garanta que existe uma EMPRESA com ID 1 no seu banco (tabela EMPRESA)
        Long empresaIdPadrao = 1L; 

        // --- CORREÇÃO AQUI ---
        // Mudei de EMPRESA_ID para ID_EMPRESA (conforme sua imagem)
        String sql = String.format(
            "INSERT INTO %s.USUARIO (NOME, EMAIL, SENHA_HASH, ID_EMPRESA, DATA_CADASTRO) " +
            "VALUES (?, ?, ?, ?, SYSDATE)",
            this.userSchema
        );

        int affectedRows = jdbcTemplate.update(
            sql,
            request.nome(),
            request.email(),
            encodedPassword,
            empresaIdPadrao
        );

        if (affectedRows == 1) {
            logger.info("Usuário cadastrado com sucesso (empresa ID {}): {}", empresaIdPadrao, request.email());
        } else {
            logger.error("Falha na persistência do cadastro. Linhas afetadas: {}", affectedRows);
        }
        return affectedRows;
    }
}