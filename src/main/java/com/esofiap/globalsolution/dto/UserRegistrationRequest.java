package com.esofiap.globalsolution.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO para receber dados de uma nova solicitação de cadastro de usuário.
 * Aplica validações de segurança e integridade de dados usando o Bean Validation.
 */
public record UserRegistrationRequest(
        // @NotBlank garante que o campo não é null e tem pelo menos um caractere não-whitespace
        @NotBlank(message = "O nome não pode ser vazio.")
        @Size(max = 50, message = "O nome deve ter no máximo 50 caracteres.")
        String nome,

        @NotBlank(message = "O email é obrigatório.")
        @Email(message = "O formato do email é inválido.")
        @Size(max = 120, message = "O email deve ter no máximo 120 caracteres.")
        String email,

        @NotBlank(message = "A senha é obrigatória.")
        // Requisito de segurança: senha deve ter no mínimo 8 caracteres
        @Size(min = 8, max = 50, message = "A senha deve ter entre 8 e 50 caracteres.")
        String senha
) {
}