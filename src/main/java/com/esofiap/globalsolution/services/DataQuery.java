package com.esofiap.globalsolution.services;

import java.util.List;
import java.util.Map;

/**
 * Serviços de execução segura de comandos de consulta (SELECT).
 */
public interface DataQuery {

    /**
     * Executa uma consulta SQL e retorna uma lista.
     * @param query A string SQL com placeholders (se houver).
     * @param args Os valores a serem substituídos nos placeholders.
     * @return Uma lista de Maps contendo os resultados da consulta.
     */
    List<Map<String, Object>> executeQuery(String query, Object... args);

    /**
     * Consulta os nomes de todas as tabelas visíveis ao usuário no banco de dados.
     * @param owner Opcional. O nome do esquema (schema) do qual as tabelas devem ser consultadas.
     * @return Uma lista de Strings contendo os nomes das tabelas.
     */
    List<String> getAllTableNames(String owner);
}