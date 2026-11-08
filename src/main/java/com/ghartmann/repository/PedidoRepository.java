package com.ghartmann.repository;

import com.ghartmann.domain.Pedido;
import com.ghartmann.domain.StatusPedido;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PedidoRepository extends JpaRepository<Pedido, Long> {
    List<Pedido> findByStatusInOrderByCriadoEmAsc(List<StatusPedido> status);
    List<Pedido> findAllByOrderByCriadoEmDesc();
}
