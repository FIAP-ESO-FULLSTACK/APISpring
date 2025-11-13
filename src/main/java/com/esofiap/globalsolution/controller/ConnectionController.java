package com.esofiap.globalsolution.controller;

import com.esofiap.globalsolution.services.ConnectivityChecker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Controller REST para verificar o status com o banco de dados.
 */
@RestController
@RequestMapping("/api/status")
public class ConnectionController {

    private static final Logger logger = LoggerFactory.getLogger(ConnectionController.class);

    private final ConnectivityChecker connectionChecker;

    /**
     * Construtor para injeção de dependência.
     * @param connectionChecker A interface de verificação de conectividade.
     */
    @Autowired
    public ConnectionController(ConnectivityChecker connectionChecker) {
        this.connectionChecker = connectionChecker;
    }

    /**
     * Endpoint para verificar se a conexão com o banco de dados está ativa.
     * Mapeado para GET /api/status/db-health
     * @return Uma resposta HTTP indicando o status da conexão (200 OK ou 503 SERVICE UNAVAILABLE).
     */
    @GetMapping("/db-teste")
    public ResponseEntity<String> checkDbConnection() {
        logger.info("Tentativa de Health Check do banco de dados solicitada.");

        boolean isConnected = connectionChecker.testConnection();

        if (isConnected) {
            return new ResponseEntity<>("Conexão com o Banco de Dados Oracle: OK.", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Conexão com o Banco de Dados Oracle: FALHA. Verifique os logs e o application.properties.", HttpStatus.SERVICE_UNAVAILABLE);
        }
    }
}