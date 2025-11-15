package com.esofiap.globalsolution.controller;

import com.esofiap.globalsolution.services.DataQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Controller para endpoints de consulta e metadados do banco de dados.
 */
@RestController
@RequestMapping("/api/metadata")
public class TableController {

    private static final Logger logger = LoggerFactory.getLogger(TableController.class);
    private final DataQuery dataQuery;
    private final String defaultSchema;

    @Autowired
    public TableController(DataQuery dataQuery,
                           @Value("${app.oracle.default-schema}") String defaultSchema) {
        this.dataQuery = dataQuery;
        this.defaultSchema = defaultSchema.toUpperCase();
    }

    /**
     * Endpoint que lista todas as tabelas visíveis no banco de dados.
     * Mapeado para GET /api/metadata/tables
     * @param owner Opcional. O nome do esquema. Se não for fornecido, usa o defaultSchema.
     * @return Uma lista JSON de nomes de tabelas.
     */
    @GetMapping("/tables")
    public ResponseEntity<List<String>> listAllTables(
            @RequestParam(required = false) String owner) {

        String schemaToUse = (owner != null && !owner.trim().isEmpty())
                ? owner.toUpperCase()
                : this.defaultSchema;

        logger.info("Consulta de nomes de tabelas solicitada. Schema: {}", schemaToUse);

        List<String> tables = dataQuery.getAllTableNames(schemaToUse);

        return ResponseEntity.ok(tables);
    }


    /**
     * Endpoint que executa uma consulta SELECT * em uma tabela específica.
     * Mapeado para GET /api/metadata/data/{tableName}
     * @param tableName O nome da tabela a ser consultada.
     * @param owner Opcional. O nome do esquema. Se não for fornecido, usa o defaultSchema.
     * @return Uma lista JSON contendo os dados da tabela.
     */
    @GetMapping("/data/{tableName}")
    public ResponseEntity<List<Map<String, Object>>> getTableData(
            @PathVariable String tableName,
            @RequestParam(required = false) String owner) {

        String schemaToUse = (owner != null && !owner.trim().isEmpty())
                ? owner.toUpperCase()
                : this.defaultSchema;

        String sql = String.format("SELECT * FROM %s.%s", schemaToUse, tableName);

        logger.info("Consulta de dados solicitada para a tabela: {}.{}", schemaToUse, tableName);

        List<Map<String, Object>> data = dataQuery.executeQuery(sql);

        if (data.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(data);
    }
}