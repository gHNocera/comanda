# Comanda

Sistema local de pedidos para restaurante com duas telas:

- **Garçom/tablet:** número da mesa, quantidade de pessoas, localização, cardápio com fotos e carrinho.
- **Cozinha/monitor:** pedidos ativos, mudança para preparando, pronto e entregue.

## Requisitos

- Java 21
- Maven (o wrapper `mvnw.cmd` já acompanha o projeto)

## Executar

```powers
.\mvnw.cmd spring-boot:run
```

Abra [http://localhost:8080](http://localhost:8080).

Os pedidos e pratos de demonstração são persistidos localmente em `data/comanda.mv.db`.

## API principal

- `GET /api/pratos`
- `POST /api/pedidos`
- `GET /api/pedidos`
- `GET /api/pedidos/ativos`
- `PATCH /api/pedidos/{id}/status`
