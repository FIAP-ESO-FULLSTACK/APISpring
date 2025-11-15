package com.esofiap.globalsolution.services;

import com.esofiap.globalsolution.dto.UserRegistrationRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Serviço de aplicação responsável pela lógica de negócio de Usuários.
 * Lida com validações adicionais, codificação de senha e persistência.
 */
@Service
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);
    private final DataUpdater dataUpdater;
    private final String userSchema;

    @Autowired
    public UserService(DataUpdater dataUpdater,
                       @Value("${app.oracle.default-schema}") String defaultSchema) {
        this.dataUpdater = dataUpdater;
        this.userSchema = defaultSchema.toUpperCase();
    }

    /**
     * Realiza o cadastro seguro de um novo usuário.
     * @param request O DTO com os dados do usuário.
     * @return O número de linhas afetadas (1 se o cadastro for bem-sucedido).
     */
    public int registerNewUser(UserRegistrationRequest request) {

        String encodedPassword = request.senha();

        logger.warn("AVISO: A senha está sendo armazenada como um texto prefixado para demonstração. Use BCryptPasswordEncoder em produção.");

        // 2. Monta a Query SQL Segura
        // NOTA: Os nomes das colunas e da tabela devem ser ajustados para o seu banco Oracle.
        String sql = String.format(
                "INSERT INTO %s.USUARIO (NOME, EMAIL, SENHA_HASH, DATA_CADASTRO) VALUES (?, ?, ?, SYSDATE)",
                this.userSchema
        );

        // 3. Executa o comando DML usando o DataUpdater (PreparedStatement)
        int affectedRows = dataUpdater.executeUpdate(
                sql,
                request.nome(),
                request.email(),
                encodedPassword
        );

        if (affectedRows == 1) {
            logger.info("Usuário cadastrado com sucesso: {}", request.email());
        } else {
            logger.error("Falha na persistência do cadastro. Linhas afetadas: {}", affectedRows);
        }

        return affectedRows;
    }
}