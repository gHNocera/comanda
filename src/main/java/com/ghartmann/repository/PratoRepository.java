package com.ghartmann.repository;

import com.ghartmann.domain.Prato;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PratoRepository extends JpaRepository<Prato, Long> {
    List<Prato> findByDisponivelTrueOrderByCategoriaAscNomeAsc();
}
