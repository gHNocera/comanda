package com.ghartmann.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.ghartmann.domain.Restaurante;

public interface RestauranteRepository extends JpaRepository<Restaurante, Integer> {

    @Query("SELECT CASE WHEN COUNT(u) > 0 THEN TRUE ELSE FALSE END FROM Restaurante u WHERE u.email = :email")
    boolean existsByEmail(String email);

    Optional<Restaurante> findByEmail(String email);

}
