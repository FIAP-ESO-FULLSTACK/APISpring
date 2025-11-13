package com.esofiap.globalsolution.controller;

import com.esofiap.globalsolution.services.DataQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Controller para endpoints de consulta e metadados do banco de dados.
 */
@RestController
@RequestMapping("/api/metadata")
public class TableController {

    private static final Logger logger = LoggerFactory.getLogger(TableController.class);
    private final DataQuery dataQuery;

    @Autowired
    public TableController(DataQuery dataQuery) {
        this.dataQuery = dataQuery;
    }

    /**
     * Endpoint que lista todas as tabelas visíveis no banco de dados, com opção de filtrar por OWNER.
     * Mapeado para GET /api/metadata/tables
     * @param owner Opcional. O nome do esquema para filtrar as tabelas.
     * @return Uma lista JSON de nomes de tabelas.
     */
    @GetMapping("/tables")
    public ResponseEntity<List<String>> listAllTables(
            @RequestParam(required = false) String owner) {

        logger.info("Consulta de todas as tabelas solicitada. Owner: {}", owner != null ? owner : "Todos");

        List<String> tables = dataQuery.getAllTableNames(owner);

        return ResponseEntity.ok(tables);
    }
}