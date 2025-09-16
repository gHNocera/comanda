package com.ghartmann.service;

import org.springframework.stereotype.Service;

import com.ghartmann.domain.Restaurante;
import com.ghartmann.repository.RestauranteRepository;
import com.ghartmann.validations.RestauranteValidations;

@Service
public class RestauranteService {

    private final RestauranteRepository restauranteRepository;
    private final RestauranteValidations restauranteValidations;

    public RestauranteService(RestauranteRepository restauranteRepository, RestauranteValidations restauranteValidations) {
        this.restauranteRepository = restauranteRepository;
        this.restauranteValidations = restauranteValidations;
    }

    /*Cadastra o restaurante no banco de dados */
    public void cadastrarRestaurante(Restaurante restaurante) {
        restauranteValidations.validateRestaurante(restaurante);
        restauranteRepository.save(restaurante);
    }

    /*Realiza o login do Restaurante */
    public Restaurante login(String email, String senha) {
        Restaurante restaurante = restauranteRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Email ou senha inválidos."));
        return restaurante;
    }

    /*Atualiza o Restaurante */
    public void atualizarRestaurante(Restaurante restaurante) {
        Restaurante existingRestaurante = restauranteRepository.findById(restaurante.getId())
                .orElseThrow(() -> new IllegalArgumentException("Restaurante não encontrado."));
        restauranteValidations.validateRestaurante(restaurante);
        restauranteRepository.save(restaurante);
    }
}
