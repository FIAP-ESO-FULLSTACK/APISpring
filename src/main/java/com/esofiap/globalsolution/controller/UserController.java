package com.esofiap.globalsolution.controller;

import com.esofiap.globalsolution.dto.UserRegistrationRequest;
import com.esofiap.globalsolution.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid; // Importante para ativar a validação do DTO

/**
 * Controller REST para gerenciar operações de Usuários, como cadastro e login.
 */
@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    /**
     * Construtor para injeção de dependência do UserService.
     */
    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Endpoint de cadastro de novo usuário.
     * A anotação @Valid ativa as regras de validação definidas no DTO (UserRegistrationRequest).
     * @param request Dados do usuário para cadastro.
     * @return 201 Created em caso de sucesso ou 500 Internal Server Error em caso de falha na persistência.
     */
    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@Valid @RequestBody UserRegistrationRequest request) {

        // Chamada para o serviço que contém a lógica de segurança (hashing) e persistência
        int affectedRows = userService.registerNewUser(request);

        if (affectedRows == 1) {

            // Retorna 201 CREATED
            return new ResponseEntity<>("Usuário criado com sucesso.", HttpStatus.CREATED);
        } else {
            // Se o affectedRows for 0, algo falhou na execução do INSERT
            return new ResponseEntity<>("Falha ao registrar usuário. Nenhuma linha afetada.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}