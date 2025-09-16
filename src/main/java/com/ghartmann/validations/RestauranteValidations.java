package com.ghartmann.validations;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ghartmann.domain.Restaurante;
import com.ghartmann.repository.RestauranteRepository;

@Component
public class RestauranteValidations {

    private final RestauranteRepository restauranteRepository;

    @Autowired
    public RestauranteValidations(RestauranteRepository restauranteRepository) {
        this.restauranteRepository = restauranteRepository;
    }

    public void validateRestaurante(Restaurante restaurante) {
        validateNome(restaurante.getNome());
        validateEmail(restaurante.getEmail());
        validateTelefone(restaurante.getTelefone());
        validateEndereco(restaurante.getEndereco());
        validateSenha(restaurante.getSenha());
    }

    private void validateNome(String nome) {
        if (nome == null || nome.trim().isEmpty()) {
            throw new IllegalArgumentException("Nome do restaurante não pode ser nulo ou vazio.");
        }
        if (nome.length() < 3 || nome.length() > 100) {
            throw new IllegalArgumentException("Nome do restaurante deve ter entre 3 e 100 caracteres.");
        }
        if(!nome.matches("^[A-Za-zÀ-ú0-9 ]+$")) {
            throw new IllegalArgumentException("Nome do restaurante deve conter apenas letras e espaços.");
        }
    }
    private void validateEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email do restaurante não pode ser nulo ou vazio.");
        }
        if (!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
            throw new IllegalArgumentException("Email do restaurante inválido.");
        }
        if(restauranteRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Email do restaurante já cadastrado.");
        }
    }
    private void validateTelefone(String telefone) {
        if (telefone == null || telefone.trim().isEmpty()) {
            throw new IllegalArgumentException("Telefone do restaurante não pode ser nulo ou vazio.");
        }
        if (!telefone.matches("^\\+?[0-9]{10,15}$")) {
            throw new IllegalArgumentException("Telefone do restaurante inválido.");
        }
    }
    private void validateEndereco(String endereco) {
        if (endereco == null || endereco.trim().isEmpty()) {
            throw new IllegalArgumentException("Endereço do restaurante não pode ser nulo ou vazio.");
        }
        if (endereco.length() < 5 || endereco.length() > 200) {
            throw new IllegalArgumentException("Endereço do restaurante deve ter entre 5 e 200 caracteres.");
        }
    }
    private void validateSenha(String senha) {
        if (senha == null || senha.trim().isEmpty()) {
            throw new IllegalArgumentException("Senha do restaurante não pode ser nulo ou vazio.");
        }
        if (senha.length() < 8 || senha.length() > 12) {
            throw new IllegalArgumentException("Senha do restaurante deve ter entre 8 e 12 caracteres.");
        }
        if (!senha.matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,12}$")) {
            throw new IllegalArgumentException("Senha do restaurante deve conter pelo menos uma letra maiúscula, uma letra minúscula, um número e um caractere especial.");
        }
    }


}
