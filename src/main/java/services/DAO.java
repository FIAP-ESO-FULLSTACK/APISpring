package services;

import java.sql.SQLException;

public interface DAO {

    /**
     * Testa a conectividade com o banco de dados.
     * @return {@code true} se a conexão for bem-sucedida e válida; {@code false} caso contrário.
     */
    boolean testConnection();

    /**
     * Executa uma consulta SQL simples que não retorna dados (ex: INSERT, UPDATE, DELETE).
     *  @param query A string SQL a ser executada.
     * @return O número de linhas afetadas pela execução do comando.
     * @throws SQLException Se ocorrer um erro durante a execução da consulta.
     */
    int executeUpdate(String query) throws SQLException;
}