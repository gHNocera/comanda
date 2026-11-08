const state = { pratos: [], carrinho: [], pratoModal: null, modalQuantidade: 1 };
const money = new Intl.NumberFormat("pt-BR", { style:"currency", currency:"BRL" });

document.querySelectorAll(".tab").forEach(tab => tab.addEventListener("click", () => {
  document.querySelectorAll(".tab,.view").forEach(el => el.classList.remove("active"));
  tab.classList.add("active");
  document.getElementById(tab.dataset.view).classList.add("active");
}));

async function api(url, options = {}) {
  const response = await fetch(url, { ...options, headers: { "Content-Type":"application/json", ...(options.headers || {}) } });
  const body = await response.json().catch(() => ({}));
  if (!response.ok) throw new Error(body.error || "Erro na operação.");
  return body;
}

async function carregarPratos() {
  state.pratos = await api("/api/pratos");
  const ordemCategorias = ["Bebidas", "Entradas", "Carnes", "Massas", "Vegetarianos", "Prato principal", "Sobremesas"];
  const categorias = [...new Set([
    ...ordemCategorias.filter(categoria => state.pratos.some(prato => prato.categoria === categoria)),
    ...state.pratos.map(prato => prato.categoria).filter((categoria, index, lista) => categoria && lista.indexOf(categoria) === index)
  ])];

  document.getElementById("pratos").innerHTML = categorias.map(categoria => {
    const pratosCategoria = state.pratos.filter(prato => prato.categoria === categoria);
    return `<section class="categoria-cardapio">
      <h3>${categoria}</h3>
      <div class="pratos">${pratosCategoria.map(prato => `
        <article class="prato" onclick="adicionar(${prato.id})">
          <img src="${prato.imagemUrl}" alt="${prato.nome}">
          <div class="prato-info"><strong>${prato.nome}</strong><span>${prato.descricao}</span><b>${money.format(prato.preco)}</b></div>
        </article>`).join("")}</div>
    </section>`;
  }).join("");
}

window.adicionar = id => {
  const prato = state.pratos.find(x => x.id === id);
  if (!prato) return;
  state.pratoModal = prato;
  state.modalQuantidade = 1;
  document.getElementById("modal-imagem").src = prato.imagemUrl;
  document.getElementById("modal-imagem").alt = prato.nome;
  document.getElementById("modal-categoria").textContent = prato.categoria;
  document.getElementById("modal-nome").textContent = prato.nome;
  document.getElementById("modal-descricao").textContent = prato.descricao;
  document.getElementById("modal-preco").textContent = money.format(prato.preco);
  document.getElementById("modal-observacao").value = "";
  document.getElementById("modal-quantidade").textContent = "1";
  document.getElementById("modal-edicao").hidden = true;
  document.getElementById("modal-editar").textContent = "Editar prato";
  document.getElementById("prato-modal").showModal();
};

document.getElementById("modal-editar").addEventListener("click", () => {
  const edicao = document.getElementById("modal-edicao");
  const abrindo = edicao.hidden;
  edicao.hidden = !abrindo;
  document.getElementById("modal-editar").textContent = abrindo ? "Cancelar edição" : "Editar prato";
  if (abrindo && state.pratoModal) {
    document.getElementById("editar-nome").value = state.pratoModal.nome;
    const preco = Number(state.pratoModal.preco).toFixed(2).replace(".", ",");
    document.getElementById("editar-preco").value = preco;
    document.getElementById("editar-foto").value = state.pratoModal.imagemUrl || "";
    document.getElementById("editar-foto-arquivo").value = "";
    document.getElementById("editar-descricao").value = state.pratoModal.descricao || "";
    const categoriaSelect = document.getElementById("editar-categoria");
    if (state.pratoModal.categoria && ![...categoriaSelect.options].some(option => option.value === state.pratoModal.categoria)) {
      categoriaSelect.add(new Option(state.pratoModal.categoria, state.pratoModal.categoria));
    }
    categoriaSelect.value = state.pratoModal.categoria || "";
  }
});

document.getElementById("modal-salvar").addEventListener("click", async () => {
  try {
    const precoTexto = document.getElementById("editar-preco").value.trim();
    const precoNormalizado = Number(precoTexto.replace(/\./g, "").replace(",", "."));
    if (!Number.isFinite(precoNormalizado) || precoNormalizado <= 0) {
      throw new Error("Informe um preço válido.");
    }

    const form = new FormData();
    form.append("nome", document.getElementById("editar-nome").value.trim());
    form.append("preco", precoNormalizado.toFixed(2));
    form.append("imagemUrl", document.getElementById("editar-foto").value.trim());
    form.append("descricao", document.getElementById("editar-descricao").value.trim());
    form.append("categoria", document.getElementById("editar-categoria").value);
    const arquivo = document.getElementById("editar-foto-arquivo").files[0];
    if (arquivo) form.append("foto", arquivo);

    const response = await fetch(`/api/pratos/${state.pratoModal.id}`, {
      method:"PATCH",
      body:form
    });
    const result = await response.json().catch(() => ({}));
    if (!response.ok) throw new Error(result.error || "Não foi possível salvar as alterações.");
    const prato = result;
    state.pratoModal = prato;
    document.getElementById("modal-imagem").src = prato.imagemUrl;
    document.getElementById("modal-imagem").alt = prato.nome;
    document.getElementById("modal-categoria").textContent = prato.categoria;
    document.getElementById("modal-nome").textContent = prato.nome;
    document.getElementById("modal-descricao").textContent = prato.descricao;
    document.getElementById("modal-preco").textContent = money.format(prato.preco);
    document.getElementById("modal-edicao").hidden = true;
    document.getElementById("modal-editar").textContent = "Editar prato";
    await carregarPratos();
  } catch (error) {
    alert(error.message);
  }
});

document.getElementById("modal-menos").addEventListener("click", () => {
  state.modalQuantidade = Math.max(1, state.modalQuantidade - 1);
  document.getElementById("modal-quantidade").textContent = state.modalQuantidade;
});

document.getElementById("modal-mais").addEventListener("click", () => {
  state.modalQuantidade += 1;
  document.getElementById("modal-quantidade").textContent = state.modalQuantidade;
});

document.getElementById("modal-adicionar").addEventListener("click", () => {
  const observacao = document.getElementById("modal-observacao").value.trim();
  const item = state.carrinho.find(x => x.prato.id === state.pratoModal.id && x.observacao === observacao);
  if (item) item.quantidade += state.modalQuantidade;
  else state.carrinho.push({ prato: state.pratoModal, quantidade: state.modalQuantidade, observacao });
  document.getElementById("prato-modal").close();
  renderCarrinho();
});

document.getElementById("prato-modal").addEventListener("click", event => {
  if (event.target === event.currentTarget) event.currentTarget.close();
});

document.getElementById("adicionar-mais").addEventListener("click", () => {
  document.getElementById("novo-prato-form").reset();
  document.getElementById("novo-prato-feedback").textContent = "";
  document.getElementById("novo-prato-preview").hidden = true;
  document.getElementById("novo-prato-modal").showModal();
});

document.getElementById("novo-prato-fechar").addEventListener("click", () => {
  document.getElementById("novo-prato-modal").close();
});

document.getElementById("novo-prato-modal").addEventListener("click", event => {
  if (event.target === event.currentTarget) event.currentTarget.close();
});

document.getElementById("novo-prato-imagem-url").addEventListener("input", event => {
  const preview = document.getElementById("novo-prato-preview");
  preview.src = event.target.value.trim();
  preview.hidden = !event.target.value.trim();
});

document.getElementById("novo-prato-foto").addEventListener("change", event => {
  const arquivo = event.target.files[0];
  if (!arquivo) return;
  const preview = document.getElementById("novo-prato-preview");
  preview.src = URL.createObjectURL(arquivo);
  preview.hidden = false;
});

document.getElementById("novo-prato-form").addEventListener("submit", async event => {
  event.preventDefault();
  const feedback = document.getElementById("novo-prato-feedback");
  feedback.textContent = "";

  const precoTexto = document.getElementById("novo-prato-preco").value.trim();
  const precoNormalizado = Number(precoTexto.replace(/\./g, "").replace(",", "."));
  if (!Number.isFinite(precoNormalizado) || precoNormalizado <= 0) {
    feedback.textContent = "Informe um preço válido.";
    return;
  }

  const form = new FormData();
  form.append("nome", document.getElementById("novo-prato-nome").value.trim());
  form.append("preco", precoNormalizado.toFixed(2));
  form.append("descricao", document.getElementById("novo-prato-descricao").value.trim());
  form.append("categoria", document.getElementById("novo-prato-categoria").value);
  form.append("imagemUrl", document.getElementById("novo-prato-imagem-url").value.trim());
  const foto = document.getElementById("novo-prato-foto").files[0];
  if (foto) form.append("foto", foto);

  try {
    const response = await fetch("/api/pratos", { method:"POST", body:form });
    const result = await response.json().catch(() => ({}));
    if (!response.ok) throw new Error(result.error || "Não foi possível adicionar o prato.");
    await carregarPratos();
    document.getElementById("novo-prato-modal").close();
  } catch (error) {
    feedback.textContent = error.message;
  }
});

function renderCarrinho() {
  document.getElementById("carrinho").innerHTML = state.carrinho.length ? state.carrinho.map((item, index) => `
    <div class="cart-item">
      <strong>${item.prato.nome}</strong>
      <div class="qty">
        <button onclick="alterarQuantidade(${index},-1)">−</button><b>${item.quantidade}</b>
        <button onclick="alterarQuantidade(${index},1)">+</button>
      </div>
      <button onclick="alterarQuantidade(${index},-${item.quantidade})" style="border:0;background:transparent;color:#b91c1c;cursor:pointer">×</button>
      ${item.observacao ? `<span class="cart-observacao"><strong>Obs:</strong> ${item.observacao}</span>` : ""}
    </div>`).join("") : '<p style="color:#817367">Selecione os pratos do cardápio.</p>';
}

window.alterarQuantidade = (index, delta) => {
  state.carrinho[index].quantidade += delta;
  if (state.carrinho[index].quantidade <= 0) state.carrinho.splice(index, 1);
  renderCarrinho();
};

document.getElementById("enviar").addEventListener("click", async () => {
  const feedback = document.getElementById("garcom-feedback");
  try {
    const pedido = await api("/api/pedidos", {
      method:"POST",
      body:JSON.stringify({
        numeroMesa:Number(document.getElementById("mesa").value),
        quantidadePessoas:Number(document.getElementById("pessoas").value),
        observacao:document.getElementById("observacao").value,
        itens:state.carrinho.map(item => ({ pratoId:item.prato.id, quantidade:item.quantidade, observacao:item.observacao }))
      })
    });
    state.carrinho = [];
    renderCarrinho();
    document.getElementById("observacao").value = "";
    feedback.style.color="#166534";
    feedback.textContent = `Pedido #${pedido.id} enviado para a cozinha.`;
    carregarPedidos();
  } catch (error) {
    feedback.style.color="#b91c1c";
    feedback.textContent = error.message;
  }
});

async function alterarStatus(id, status) {
  try {
    await api(`/api/pedidos/${id}/status`, { method:"PATCH", body:JSON.stringify({status}) });
    carregarPedidos();
  } catch (error) { alert(error.message); }
}

async function carregarPedidos() {
  const pedidos = await api("/api/pedidos/ativos");
  const cozinha = document.getElementById("pedidos-cozinha");
  cozinha.innerHTML = pedidos.length ? pedidos.map(pedido => {
    const statusLabel = pedido.status === "RECEBIDO" ? "Recebido" : pedido.status === "PREPARANDO" ? "Preparando" : "Pronto";
    const actions = pedido.status === "RECEBIDO"
      ? `<button class="prepare" onclick="alterarStatus(${pedido.id},'PREPARANDO')">Começar preparo</button>`
      : pedido.status === "PREPARANDO"
      ? `<button class="ready" onclick="alterarStatus(${pedido.id},'PRONTO')">Pedido pronto</button>`
      : "";
    return `<article class="pedido-card ${pedido.status.toLowerCase()}">
      <div class="pedido-top"><div><h2>Mesa ${pedido.numeroMesa}</h2><span>${pedido.quantidadePessoas} pessoas</span></div><span class="status">${statusLabel}</span></div>
      ${pedido.itens.map(item => `<div class="pedido-item"><strong>${item.quantidade}× ${item.prato.nome}</strong>${item.observacao ? `<span>${item.observacao}</span>` : ""}</div>`).join("")}
      ${pedido.observacao ? `<p><strong>Observação:</strong> ${pedido.observacao}</p>` : ""}
      <div class="pedido-actions">${actions}</div>
    </article>`;
  }).join("") : '<div class="empty"><h2>Nenhum pedido na cozinha</h2><p>Novos pedidos aparecerão automaticamente.</p></div>';

  document.getElementById("prontos").innerHTML = pedidos.filter(p => p.status === "PRONTO").map(p => `
    <div class="ready-card">
      <strong>Mesa ${p.numeroMesa}</strong>
      <span>${p.quantidadePessoas} pessoas</span>
      <button onclick="alterarStatus(${p.id},'ENTREGUE')">Confirmar entregue na mesa</button>
    </div>`).join("") || '<p style="color:#817367">Nenhum pedido pronto.</p>';
}

window.alterarStatus = alterarStatus;
setInterval(() => {
  carregarPedidos().catch(() => {});
  document.getElementById("relogio").textContent = new Date().toLocaleTimeString("pt-BR", {hour:"2-digit",minute:"2-digit"});
}, 4000);

carregarPratos().catch(error => document.getElementById("pratos").textContent = error.message);
carregarPedidos().catch(() => {});
document.getElementById("relogio").textContent = new Date().toLocaleTimeString("pt-BR", {hour:"2-digit",minute:"2-digit"});
