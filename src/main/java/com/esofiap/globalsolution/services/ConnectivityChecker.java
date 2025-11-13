package com.esofiap.globalsolution.services;

/**
 * Define o contrato para serviços de verificação de conectividade com banco de dados.
 */
public interface ConnectivityChecker {

    /**
     * Testa a conectividade com o banco de dados.
     * @return {@code true} se a conexão for válida; {@code false} caso contrário.
     */
    boolean testConnection();
}