package com.ghartmann.service;

import com.ghartmann.domain.Pedido;
import com.ghartmann.domain.PedidoItem;
import com.ghartmann.domain.Prato;
import com.ghartmann.domain.StatusPedido;
import com.ghartmann.repository.PedidoRepository;
import com.ghartmann.repository.PratoRepository;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
public class PedidoService {
    private final PedidoRepository pedidoRepository;
    private final PratoRepository pratoRepository;

    public PedidoService(PedidoRepository pedidoRepository, PratoRepository pratoRepository) {
        this.pedidoRepository = pedidoRepository;
        this.pratoRepository = pratoRepository;
    }

    public List<Prato> listarPratos() {
        return pratoRepository.findByDisponivelTrueOrderByCategoriaAscNomeAsc();
    }

    @Transactional
    public void excluirPrato(Long pratoId) {
        Prato prato = pratoRepository.findById(pratoId)
                .orElseThrow(() -> new IllegalArgumentException("Prato não encontrado."));
        pratoRepository.delete(prato);
    }

    @Transactional
    public Prato criarPrato(String nome, String descricao, BigDecimal preco, String imagemUrl,
                            String categoria, MultipartFile foto) {
        if (nome == null || nome.trim().isEmpty()) throw new IllegalArgumentException("Informe o nome do prato.");
        if (preco == null || preco.signum() <= 0) throw new IllegalArgumentException("Informe um preço válido.");
        if (categoria == null || categoria.trim().isEmpty()) throw new IllegalArgumentException("Informe a categoria do prato.");

        String caminhoImagem = salvarImagem(imagemUrl, foto);

        Prato prato = new Prato(nome.trim(), descricao == null ? "" : descricao.trim(),
                preco, caminhoImagem, categoria.trim());
        return pratoRepository.save(prato);
    }

    @Transactional
    public Prato editarPrato(Long pratoId, String nome, String descricao, BigDecimal preco,
                             String imagemUrl, String categoria, MultipartFile foto) {
        Prato prato = pratoRepository.findById(pratoId)
                .orElseThrow(() -> new IllegalArgumentException("Prato não encontrado."));
        if (nome == null || nome.trim().isEmpty()) throw new IllegalArgumentException("Informe o nome do prato.");
        if (preco == null || preco.signum() <= 0) throw new IllegalArgumentException("Informe um preço válido.");
        if (categoria == null || categoria.trim().isEmpty()) throw new IllegalArgumentException("Informe a categoria do prato.");

        String caminhoImagem = imagemUrl == null ? "" : imagemUrl.trim();
        if (foto != null && !foto.isEmpty()) caminhoImagem = salvarImagem("", foto);

        prato.setNome(nome.trim());
        prato.setDescricao(descricao == null ? "" : descricao.trim());
        prato.setPreco(preco);
        prato.setImagemUrl(caminhoImagem);
        prato.setCategoria(categoria.trim());
        return pratoRepository.save(prato);
    }

    private String salvarImagem(String imagemUrl, MultipartFile foto) {
        String caminhoImagem = imagemUrl == null ? "" : imagemUrl.trim();
        if (foto == null || foto.isEmpty()) return caminhoImagem;

        String nomeOriginal = foto.getOriginalFilename();
        String extensao = nomeOriginal != null && nomeOriginal.lastIndexOf('.') >= 0
                ? nomeOriginal.substring(nomeOriginal.lastIndexOf('.')).toLowerCase()
                : ".jpg";
        if (!List.of(".jpg", ".jpeg", ".png", ".webp", ".gif").contains(extensao)) {
            throw new IllegalArgumentException("Selecione uma imagem JPG, PNG, WEBP ou GIF.");
        }
        try {
            Path pasta = Path.of("data", "uploads");
            Files.createDirectories(pasta);
            String arquivo = UUID.randomUUID() + extensao;
            Files.copy(foto.getInputStream(), pasta.resolve(arquivo), StandardCopyOption.REPLACE_EXISTING);
            return "/uploads/" + arquivo;
        } catch (Exception exception) {
            throw new IllegalArgumentException("Não foi possível salvar a imagem.");
        }
    }

    @Transactional
    public Prato editarPrato(Long pratoId, Prato dados) {
        Prato prato = pratoRepository.findById(pratoId)
                .orElseThrow(() -> new IllegalArgumentException("Prato não encontrado."));
        if (dados.getNome() == null || dados.getNome().trim().isEmpty()) {
            throw new IllegalArgumentException("Informe o nome do prato.");
        }
        if (dados.getPreco() == null || dados.getPreco().signum() <= 0) {
            throw new IllegalArgumentException("Informe um preço válido.");
        }
        if (dados.getCategoria() == null || dados.getCategoria().trim().isEmpty()) {
            throw new IllegalArgumentException("Informe a categoria do prato.");
        }
        prato.setNome(dados.getNome().trim());
        prato.setDescricao(dados.getDescricao() == null ? "" : dados.getDescricao().trim());
        prato.setPreco(dados.getPreco());
        prato.setImagemUrl(dados.getImagemUrl() == null ? "" : dados.getImagemUrl().trim());
        prato.setCategoria(dados.getCategoria().trim());
        return pratoRepository.save(prato);
    }

    public List<Pedido> listarTodos() {
        return pedidoRepository.findAllByOrderByCriadoEmDesc();
    }

    public List<Pedido> listarAtivos() {
        return pedidoRepository.findByStatusInOrderByCriadoEmAsc(
                List.of(StatusPedido.RECEBIDO, StatusPedido.PREPARANDO, StatusPedido.PRONTO));
    }

    @Transactional
    public Pedido criarPedido(int numeroMesa, int quantidadePessoas, String observacao,
                              List<ItemPedido> itens) {
        if (numeroMesa <= 0) throw new IllegalArgumentException("Informe o número da mesa.");
        if (quantidadePessoas <= 0) throw new IllegalArgumentException("Informe a quantidade de pessoas.");
        if (itens == null || itens.isEmpty()) throw new IllegalArgumentException("Selecione pelo menos um prato.");

        Pedido pedido = new Pedido();
        pedido.setNumeroMesa(numeroMesa);
        pedido.setQuantidadePessoas(quantidadePessoas);
        pedido.setObservacao(observacao == null ? "" : observacao.trim());

        for (ItemPedido item : itens) {
            if (item.quantidade() <= 0) throw new IllegalArgumentException("A quantidade deve ser maior que zero.");
            Prato prato = pratoRepository.findById(item.pratoId())
                    .orElseThrow(() -> new IllegalArgumentException("Prato não encontrado: " + item.pratoId()));
            if (!prato.isDisponivel()) throw new IllegalArgumentException("Prato indisponível: " + prato.getNome());
            pedido.adicionarItem(new PedidoItem(prato, item.quantidade(),
                    item.observacao() == null ? "" : item.observacao().trim()));
        }
        return pedidoRepository.save(pedido);
    }

    @Transactional
    public Pedido alterarStatus(Long pedidoId, StatusPedido novoStatus) {
        Pedido pedido = pedidoRepository.findById(pedidoId)
                .orElseThrow(() -> new IllegalArgumentException("Pedido não encontrado."));
        pedido.setStatus(novoStatus);
        return pedidoRepository.save(pedido);
    }

    public record ItemPedido(Long pratoId, int quantidade, String observacao) {}
}
