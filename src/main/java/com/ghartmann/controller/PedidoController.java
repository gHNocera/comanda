package com.ghartmann.controller;

import com.ghartmann.domain.Pedido;
import com.ghartmann.domain.Prato;
import com.ghartmann.domain.StatusPedido;
import com.ghartmann.service.PedidoService;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import java.math.BigDecimal;

@RestController
@RequestMapping("/api")
public class PedidoController {
    private final PedidoService pedidoService;

    public PedidoController(PedidoService pedidoService) {
        this.pedidoService = pedidoService;
    }

    @GetMapping("/pratos")
    public List<Prato> listarPratos() {
        return pedidoService.listarPratos();
    }

    @PostMapping(value = "/pratos", consumes = "multipart/form-data")
    public ResponseEntity<?> criarPrato(
            @RequestParam String nome,
            @RequestParam(required = false, defaultValue = "") String descricao,
            @RequestParam BigDecimal preco,
            @RequestParam(required = false, defaultValue = "") String imagemUrl,
            @RequestParam String categoria,
            @RequestParam(required = false) MultipartFile foto) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(pedidoService.criarPrato(nome, descricao, preco, imagemUrl, categoria, foto));
        } catch (IllegalArgumentException exception) {
            return ResponseEntity.badRequest().body(Map.of("error", exception.getMessage()));
        }
    }

    @PatchMapping("/pratos/{id}")
    public ResponseEntity<?> editarPrato(@PathVariable Long id, @RequestBody Prato prato) {
        try {
            return ResponseEntity.ok(pedidoService.editarPrato(id, prato));
        } catch (IllegalArgumentException exception) {
            return ResponseEntity.badRequest().body(Map.of("error", exception.getMessage()));
        }
    }

    @PatchMapping(value = "/pratos/{id}", consumes = "multipart/form-data")
    public ResponseEntity<?> editarPratoComImagem(
            @PathVariable Long id,
            @RequestParam String nome,
            @RequestParam(required = false, defaultValue = "") String descricao,
            @RequestParam BigDecimal preco,
            @RequestParam(required = false, defaultValue = "") String imagemUrl,
            @RequestParam String categoria,
            @RequestParam(required = false) MultipartFile foto) {
        try {
            return ResponseEntity.ok(pedidoService.editarPrato(
                    id, nome, descricao, preco, imagemUrl, categoria, foto));
        } catch (IllegalArgumentException exception) {
            return ResponseEntity.badRequest().body(Map.of("error", exception.getMessage()));
        }
    }

    @DeleteMapping("/pratos/{id}")
    public ResponseEntity<?> excluirPrato(@PathVariable Long id) {
        try {
            pedidoService.excluirPrato(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException exception) {
            return ResponseEntity.badRequest().body(Map.of("error", exception.getMessage()));
        }
    }

    @GetMapping("/pedidos")
    public List<Pedido> listarPedidos() {
        return pedidoService.listarTodos();
    }

    @GetMapping("/pedidos/ativos")
    public List<Pedido> listarAtivos() {
        return pedidoService.listarAtivos();
    }

    @PostMapping("/pedidos")
    public ResponseEntity<?> criarPedido(@RequestBody NovoPedido novoPedido) {
        try {
            Pedido pedido = pedidoService.criarPedido(
                    novoPedido.numeroMesa(),
                    novoPedido.quantidadePessoas(),
                    novoPedido.observacao(),
                    novoPedido.itens());
            return ResponseEntity.status(HttpStatus.CREATED).body(pedido);
        } catch (IllegalArgumentException exception) {
            return ResponseEntity.badRequest().body(Map.of("error", exception.getMessage()));
        }
    }

    @PatchMapping("/pedidos/{id}/status")
    public ResponseEntity<?> alterarStatus(@PathVariable Long id, @RequestBody StatusRequest request) {
        try {
            Pedido pedido = pedidoService.alterarStatus(id, StatusPedido.valueOf(request.status().trim().toUpperCase()));
            return ResponseEntity.ok(pedido);
        } catch (IllegalArgumentException exception) {
            return ResponseEntity.badRequest().body(Map.of("error", "Status inválido ou pedido inexistente."));
        }
    }

    public record NovoPedido(int numeroMesa, int quantidadePessoas, String observacao,
                             List<PedidoService.ItemPedido> itens) {}
    public record StatusRequest(String status) {}
}
