package com.esofiap.globalsolution.services;

import com.esofiap.globalsolution.dto.UserRegistrationRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.jdbc.core.JdbcTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Serviço de aplicação responsável pela lógica de negócio de Usuários.
 * Lida com validações adicionais, codificação de senha e persistência.
 */
@Service
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);
    private final JdbcTemplate jdbcTemplate;
    private final String userSchema;
    private final PasswordEncoder passwordEncoder;

    /**
     * Construtor atualizado para injetar o PasswordEncoder
     */
    public UserService(JdbcTemplate jdbcTemplate,
                       @Value("${app.oracle.default-schema}") String defaultSchema,
                       PasswordEncoder passwordEncoder) {
        this.jdbcTemplate = jdbcTemplate;
        this.userSchema = defaultSchema.toUpperCase();
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Realiza o cadastro seguro de um novo usuário.
     * @param request O DTO com os dados do usuário.
     * @return O número de linhas afetadas (1 se o cadastro for bem-sucedido).
     */
    public int registerNewUser(UserRegistrationRequest request) {

        // --- FAZENDO O HASH DA SENHA ---
        String encodedPassword = passwordEncoder.encode(request.senha());

        // 2. Monta a Query SQL Segura
        String sql = String.format(
                "INSERT INTO %s.USUARIO (NOME, EMAIL, SENHA_HASH, DATA_CADASTRO) VALUES (?, ?, ?, SYSDATE)",
                this.userSchema
        );

        // 3. Executa o comando DML usando o JdbcTemplate
        int affectedRows = jdbcTemplate.update(
                sql,
                request.nome(),
                request.email(),
                encodedPassword // <-- Enviando o hash
        );

        if (affectedRows == 1) {
            logger.info("Usuário cadastrado com sucesso (com hash): {}", request.email());
        } else {
            logger.error("Falha na persistência do cadastro. Linhas afetadas: {}", affectedRows);
        }

        return affectedRows;
    }
}