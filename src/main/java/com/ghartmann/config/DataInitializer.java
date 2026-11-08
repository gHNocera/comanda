package com.ghartmann.config;

import com.ghartmann.domain.Prato;
import com.ghartmann.repository.PratoRepository;
import java.math.BigDecimal;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements ApplicationRunner {
    private final PratoRepository pratoRepository;

    public DataInitializer(PratoRepository pratoRepository) {
        this.pratoRepository = pratoRepository;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (pratoRepository.count() == 0) {
            pratoRepository.save(new Prato("Picanha na chapa", "Picanha fatiada com arroz, feijão e farofa.",
                    new BigDecimal("79.90"), "https://images.unsplash.com/photo-1544025162-d76694265947?auto=format&fit=crop&w=700&q=80", "Carnes"));
            pratoRepository.save(new Prato("Frango grelhado", "Peito grelhado, legumes e molho da casa.",
                    new BigDecimal("42.90"), "https://images.unsplash.com/photo-1532550907401-a500c9a57435?auto=format&fit=crop&w=700&q=80", "Carnes"));
            pratoRepository.save(new Prato("Risoto de cogumelos", "Arroz cremoso com cogumelos frescos e parmesão.",
                    new BigDecimal("48.50"), "https://images.unsplash.com/photo-1476124369491-e7addf5db371?auto=format&fit=crop&w=700&q=80", "Vegetarianos"));
            pratoRepository.save(new Prato("Lasanha bolonhesa", "Camadas de massa, carne, molho e queijo.",
                    new BigDecimal("46.90"), "https://images.unsplash.com/photo-1574894709920-11b28e7367e3?auto=format&fit=crop&w=700&q=80", "Massas"));
            pratoRepository.save(new Prato("Salada da casa", "Folhas, tomate, pepino, queijo e molho especial.",
                    new BigDecimal("29.90"), "https://images.unsplash.com/photo-1546793665-c74683f339c1?auto=format&fit=crop&w=700&q=80", "Entradas"));
            pratoRepository.save(new Prato("Brownie com sorvete", "Brownie de chocolate e uma bola de sorvete.",
                    new BigDecimal("24.90"), "https://images.unsplash.com/photo-1606313564200-e75d5e30476c?auto=format&fit=crop&w=700&q=80", "Sobremesas"));
        }
        if (pratoRepository.findAll().stream().noneMatch(prato -> prato.getNome().equals("Suco de laranja"))) {
            pratoRepository.save(new Prato("Suco de laranja", "Copo de 300 ml, natural e gelado.",
                    new BigDecimal("12.90"), "https://images.unsplash.com/photo-1600271886742-f049cd451bba?auto=format&fit=crop&w=700&q=80", "Bebidas"));
            pratoRepository.save(new Prato("Refrigerante", "Latinha gelada.",
                    new BigDecimal("8.00"), "https://images.unsplash.com/photo-1629203851122-3726ecdf080e?auto=format&fit=crop&w=700&q=80", "Bebidas"));
        }
    }
}
