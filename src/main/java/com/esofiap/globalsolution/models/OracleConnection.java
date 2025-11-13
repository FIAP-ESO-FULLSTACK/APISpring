package com.esofiap.globalsolution.models;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.esofiap.globalsolution.services.ConnectivityChecker;
import com.esofiap.globalsolution.services.DataUpdater;

/**
 * Gerencia o acesso ao Oracle Database usando {@code JdbcTemplate}.
 * <p>Implementa {@link ConnectivityChecker} para testar a conexão e {@link DataUpdater} para comandos DML.</p>
 */
@Service
public class OracleConnection implements ConnectivityChecker, DataUpdater {
    private static final Logger logger = LoggerFactory.getLogger(OracleConnection.class);

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public OracleConnection(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public boolean testConnection() {
        try {
            jdbcTemplate.execute("SELECT 1 FROM DUAL");
            logger.info("Conexão com Oracle é válida.");
            return true;
        } catch (Exception e) {
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
        } catch (Exception e) {
            logger.error("Falha ao executar comando SQL seguro. Query: {}", query, e);
            throw e;
        }
    }
}