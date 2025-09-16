package com.ghartmann.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ghartmann.domain.Restaurante;
import com.ghartmann.service.RestauranteService;

@RestController
@RequestMapping("/restaurantes")
public class RestauranteController {

    private final RestauranteService restauranteService;

    @Autowired
    public RestauranteController(RestauranteService restauranteService) {
        this.restauranteService = restauranteService;
    }

    @PostMapping("/cadastrar")
    public ResponseEntity<?> cadastrarRestaurante(@RequestBody Restaurante restaurante) {
        try {
            restauranteService.cadastrarRestaurante(restaurante);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Restaurante cadastrado com sucesso.");
            response.put("status", "success");
            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            errorResponse.put("status", "error");
            return ResponseEntity.badRequest().body(errorResponse);
            
        }
        
    }

}
