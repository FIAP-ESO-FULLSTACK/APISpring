package com.esofiap.globalsolution.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Implementação do UserDetailsService para carregar detalhes do usuário do banco de dados Oracle.
 * O username para login será tratado como o campo EMAIL da tabela TB_USUARIO.
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final DataQuery dataQuery;
    private final String userSchema;

    @Autowired
    public CustomUserDetailsService(DataQuery dataQuery,
                                    @Value("${app.oracle.default-schema}") String defaultSchema) {
        this.dataQuery = dataQuery;
        this.userSchema = defaultSchema.toUpperCase();
    }

    /**
     * Carrega o usuário pelo nome de usuário (neste caso, o EMAIL).
     * @param username O email fornecido durante o login.
     * @return UserDetails contendo username, senha
     * @throws UsernameNotFoundException Se o usuário não for encontrado no banco de dados.
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Monta a Query SQL Segura
        String sql = String.format(
                "SELECT EMAIL, SENHA_HASH FROM %s.USUARIO WHERE EMAIL = ?",
                this.userSchema
        );

        // Executa a consulta
        List<Map<String, Object>> result = dataQuery.executeQuery(sql, username);

        if (result.isEmpty()) {
            throw new UsernameNotFoundException("Usuário não encontrado com o email: " + username);
        }

        // Extrai os dados do primeiro resultado
        Map<String, Object> userData = result.get(0);
        String email = (String) userData.get("EMAIL");
        String senhaHash = (String) userData.get("SENHA_HASH");

        // Retorna um objeto UserDetails que o Spring Security usará para comparar a senha.
        // O último parâmetro (Collections.emptyList()) representa as autoridades/roles do usuário.
        return new User(email, senhaHash, Collections.emptyList());
    }
}