package com.esofiap.globalsolution.models;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import com.esofiap.globalsolution.services.ConnectivityChecker; // NOVO IMPORT
import com.esofiap.globalsolution.services.DataQuery;
import com.esofiap.globalsolution.services.DataUpdater;

/**
 * Gerencia o acesso ao Oracle Database usando {@code JdbcTemplate}.
 * Implementa {@link ConnectivityChecker}, {@link DataUpdater} e {@link DataQuery}.
 */
@Service
public class OracleConnection implements ConnectivityChecker, DataUpdater, DataQuery {
    private static final Logger logger = LoggerFactory.getLogger(OracleConnection.class);

    private final JdbcTemplate jdbcTemplate;

    public OracleConnection(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public boolean testConnection() {
        try {
            jdbcTemplate.execute("SELECT 1 FROM DUAL");
            logger.info("Conexão com Oracle é válida.");
            return true;
        } catch (org.springframework.dao.DataAccessException e) {
            logger.error("Erro ao testar conexão com Oracle. Verifique a URL e credenciais no application.properties.", e);
            return false;
        }
    }

    @Override
    public int executeUpdate(String query, Object... args) {
        try {
            int affectedRows = jdbcTemplate.update(query, args);
            logger.info("Comando SQL seguro executado. Linhas afetadas: {}", affectedRows);
            return affectedRows;
        } catch (org.springframework.dao.DataAccessException e) {
            logger.error("Falha ao executar comando SQL seguro. Query: {}", query, e);
            throw e;
        }
    }

    /**
     * Implementação do método de consulta genérica (DataQuery).
     * Garante a segurança usando PreparedStatement (via JdbcTemplate).
     */
    @Override
    public List<Map<String, Object>> executeQuery(String query, Object... args) {
        try {
            return jdbcTemplate.queryForList(query, args);
        } catch (org.springframework.dao.DataAccessException e) {
            logger.error("Falha ao executar consulta SQL. Query: {}", query, e);
            throw e;
        }
    }

    /**
     * {@inheritDoc}
     * <p>Consulta a view {@code ALL_TABLES} do Oracle.</p>
     */
    @Override
    public List<String> getAllTableNames(String owner) {
        String baseQuery = "SELECT table_name FROM ALL_TABLES";
        String fullQuery;

        if (owner != null && !owner.trim().isEmpty()) {
            fullQuery = baseQuery + " WHERE owner = ?";
            logger.info("Consultando tabelas para o owner: {}", owner);

            return jdbcTemplate.queryForList(fullQuery, String.class, owner.toUpperCase());
        } else {
            fullQuery = baseQuery;
            logger.warn("Consultando todas as tabelas visíveis. Para melhor performance e segurança, forneça o OWNER (Schema).");

            return jdbcTemplate.queryForList(fullQuery, String.class);
        }
    }
}