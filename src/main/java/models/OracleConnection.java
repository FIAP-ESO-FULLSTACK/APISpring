package models;

import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Service para verificar conexão com o Oracle Database e a execução de consultas simples.
 * <p>Esta classe aproveita a injeção do {@code DataSource} configurado
 * automaticamente pelo Spring Boot, lendo as propriedades {@code spring.datasource.*}
 * no {@code application.properties}.</p>
 */
@Service
public class OracleConnection {

    /**
     * O objeto DataSource, injetado pelo Spring, que gerencia
     * o pool de conexões com o banco de dados.
     */
    private final DataSource dataSource;

    /**
     * Construtor para injeção de dependência do DataSource.
     *  @param dataSource O DataSource configurado via application.properties.
     */
    @Autowired
    public OracleConnection(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    /**
     * Tenta obter uma conexão do pool e fecha-a imediatamente, simulando
     * a execução de uma consulta simples.
     *  <p>Utiliza a estrutura try-catch para garantir que a {@code Connection}
     * seja fechada e retornada ao pool, mesmo que ocorra uma exceção.</p>
     *  @see DataSource#getConnection()
     */
    public void queryExecute() {
        try (Connection connection = dataSource.getConnection()) {
            System.out.println("Conexão realizada com sucesso!");
            //TODO: adicionar
        } catch (SQLException e) {
            System.out.println( "Erro ao conectar!");
        }
    }
}