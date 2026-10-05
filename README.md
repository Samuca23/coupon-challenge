# Coupon Challenge

API de cupons de desconto — teste técnico com foco em **criação** e **exclusão** (soft delete) de cupons, com todas as regras de negócio validadas e testadas. Arquitetura hexagonal (domínio ≠ entidade JPA, use cases com responsabilidade única).

## Stack

- Java 21
- Spring Boot 4.1.1 + Maven
- H2 (banco em memória)
- springdoc-openapi (Swagger UI)
- JUnit 5 / Mockito / AssertJ + JaCoCo
- Docker + Docker Compose

## Como rodar com Docker

Único pré-requisito: **Docker** (não precisa Java nem Maven instalados).

```bash
docker compose up --build
```

Se a porta `8080` já estiver ocupada na sua máquina, mapeie outra sem editar nenhum arquivo:

```bash
APP_PORT=9090 docker compose up --build
```

O `docker-compose.yml` só tem um serviço (`app`) porque o H2 roda embutido em memória dentro do próprio processo da aplicação — não há banco separado para orquestrar.

## Como rodar localmente sem Docker

```bash
./mvnw spring-boot:run
```

Ou pelo IntelliJ: rode a classe `CouponChallengeApplication`.

A aplicação sobe em `http://localhost:8080` (ou na porta escolhida via `APP_PORT`, se for via Docker).

## Como rodar os testes e ver a cobertura

```bash
./mvnw clean verify
```

Relatório de cobertura em `target/site/jacoco/index.html`. A regra de cobertura mínima (80% de linhas) é aplicada só sobre `domain` + `application` — as camadas com regra de negócio de verdade.

## Endpoints

| Método | Rota | Descrição |
|---|---|---|
| `POST` | `/coupon` | Cria um cupom, validando código, valor de desconto e data de expiração |
| `DELETE` | `/coupon/{id}` | Deleta (soft delete) um cupom existente, transicionando o status para `DELETED` |
| `GET` | `/coupon` | Lista todos os cupons cadastrados (endpoint auxiliar, fora do escopo formal do desafio) |

## Swagger UI e H2 Console

- Swagger UI: [`http://localhost:8080/swagger-ui.html`](http://localhost:8080/swagger-ui.html)
- H2 Console: [`http://localhost:8080/h2-console`](http://localhost:8080/h2-console) — JDBC URL `jdbc:h2:mem:coupondb`, usuário `sa`, sem senha.

> ⚠️ O H2 console está configurado com `web-allow-others: true` para funcionar através do port-mapping do Docker. Isso é aceitável para este ambiente de avaliação local — não é uma configuração apropriada para produção.

## Documentação completa

Regras de negócio, decisões de arquitetura e o fluxo de desenvolvimento (spec-driven) estão documentados em [`CLAUDE.md`](./CLAUDE.md).
