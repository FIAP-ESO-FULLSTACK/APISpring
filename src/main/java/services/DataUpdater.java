package services;

/**
 * Interface que define a execução segura de comandos de modificação de dados (DML).
 */
public interface DataUpdater {

    /**
     * Executa comandos SQL DML (INSERT, UPDATE, DELETE) de forma segura.
     * @param query A string SQL com placeholders (ex: "INSERT INTO T (VAL) VALUES (?)").
     * @param args Os valores a serem substituídos nos placeholders.
     * @return O número de linhas afetadas pela execução do comando.
     */
    int executeUpdate(String query, Object... args);
}