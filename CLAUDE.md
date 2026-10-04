# Coupon Challenge — Arquivo de Conhecimento do Projeto

> Teste técnico para vaga (nível da vaga: **sênior**; requisitos formais exigidos: **Pleno**, com capricho de sênior em processo — commits pequenos, PRs bem estruturados, boas práticas de revisão).

## 1. Objetivo

Gerar um projeto Java Spring (via Spring Initializr, criado manualmente pelo Samuel em https://start.spring.io/) que implemente os endpoints de cupons de desconto descritos na documentação da API, respeitando rigorosamente as regras de negócio. O foco do desafio **não é CRUD completo** — é **Create** e **Delete** funcionando corretamente, com todas as regras de negócio validadas e testadas (comportamento, não tecnologia).

Documentação oficial da API: https://n1m0i5k0zu.apidog.io/

## 2. Stack definida

- Java 21
- Maven
- Spring Boot (gerado via Spring Initializr — Samuel cria o projeto manualmente)
- Banco em memória H2
- JUnit / testes de comportamento (cobertura ≥ 80% das regras de negócio)
- Docker + Docker Compose
- Swagger / OpenAPI

## 3. Documentação da API (o que está publicado)

Apenas o endpoint de criação está formalmente documentado no apidog. O endpoint de delete **não aparece na documentação pública** — precisa ser projetado por nós seguindo convenção REST e as regras de negócio (ver seção 5, "Decisões em aberto").

### POST /coupon

**Request body** (`application/json`):

| Campo | Tipo | Obrigatório | Exemplo |
|---|---|---|---|
| code | string | sim | `ABC-123` |
| description | string | sim | `Texto descritivo do cupom` |
| discountValue | number | sim | `0.8` |
| expirationDate | string (ISO 8601) | sim | `2025-11-04T17:14:45.180Z` |
| published | boolean | não (default `false`) | `false` |

**Response** — `201 Created`:

| Campo | Tipo | Descrição |
|---|---|---|
| id | string | UUID gerado automaticamente |
| code | string | código já normalizado (6 chars, sem especiais) |
| description | string | |
| discountValue | number | valor de desconto |
| expirationDate | string (ISO 8601) | |
| status | string | `ACTIVE`, `INACTIVE` ou `DELETED` |
| published | boolean | default `false` |
| redeemed | boolean | default `false` |

```json
{
  "id": "cef9d1e3-aae5-4ab6-a297-358c6032b1e7",
  "code": "ABC123",
  "description": "Descrição do cupom",
  "discountValue": 0.8,
  "expirationDate": "2025-11-04T17:36:46.577Z",
  "status": "ACTIVE",
  "published": false,
  "redeemed": false
}
```

> **Importante — conflito entre doc e regra de negócio:** a documentação sugere `discountValue` "recomendado entre 0 e 1", mas a regra de negócio explícita diz "saldo mínimo de 0,5 sem máximo predeterminado" e que é um **valor absoluto, sem preocupação com moeda**. A regra de negócio prevalece sobre o exemplo da doc: `discountValue >= 0.5`, sem teto.

## 4. Regras de negócio

### Create

- Campos obrigatórios: `code`, `description`, `discountValue`, `expirationDate`.
- `code`: alfanumérico, tamanho padrão de **6 caracteres**. Caracteres especiais podem vir na requisição, mas a aplicação deve **removê-los antes de salvar e antes de retornar na resposta**, garantindo 6 caracteres no resultado final.
- `discountValue`: mínimo **0,5** (absoluto, sem moeda), sem máximo.
- `expirationDate`: **nunca** pode ser uma data no passado (validar contra o momento da criação).
- Um cupom **pode** ser criado já com `published = true`.
- Status retornado automaticamente como `ACTIVE` na criação.

### Delete

- Pode ser deletado a qualquer momento.
- **Soft delete**: o registro não pode ser perdido do banco — provavelmente via transição de `status` para `DELETED` (o enum de status já inclui `ACTIVE / INACTIVE / DELETED`, então soft delete = mudança de status, não uma flag `deleted` separada nem remoção física).
- **Não é possível deletar um cupom já deletado** — isso é regra de domínio, não só uma checagem de infraestrutura, e precisa ser testada (vão tentar "quebrar" essa regra).
- **Concorrência (decisão de estratégia):** a regra acima, sozinha, só cobre duas chamadas *sequenciais* (`Coupon.delete()` no domínio lança exceção se o status já é `DELETED`). Pra cobrir duas chamadas *simultâneas* no mesmo cupom (race condition: ambas leem `ACTIVE` antes de qualquer uma salvar), a entidade JPA (`CouponJpaEntity`, infraestrutura — nunca o `Coupon` de domínio) ganha um campo `@Version`. O adapter de persistência, ao salvar, trata a `OptimisticLockingFailureException` do Spring Data: recarrega o cupom e, se o status atual já é `DELETED`, relança a mesma exceção de domínio de "já deletado" (mesmo contrato de erro pro cliente); senão, propaga o erro normalmente. Domínio decide a regra, banco garante que ela sobrevive à concorrência real.

## 5. Decisões em aberto (a validar com o Samuel antes/durante o desenvolvimento)

- Path e verbo do endpoint de delete (não documentado publicamente). Hipótese mais provável: `DELETE /coupon/{id}` → `204 No Content`, ou `200` retornando o cupom com `status: DELETED`. A ser confirmado.
- Normalização do `code`: regra diz "remover especiais garantindo 6 caracteres", mas não especifica o que fazer se, depois de remover os especiais, sobrarem menos de 6 alfanuméricos (padding? erro de validação?). Tratar como regra de domínio explícita no value object do código, com decisão documentada no código/teste.
- Tentar deletar um cupom que **não existe** (id inválido) — comportamento não especificado nas regras; decidir e documentar (provável `404`).

## 6. Expectativas de entrega (nível Pleno + capricho sênior)

- [ ] Testes cobrindo as regras de negócio (meta: 80%+), focados em **comportamento** (o que pode/não pode acontecer), não em tecnologia. Mocks sozinhos não bastam — cobrir os casos de borda que "tentam quebrar" as regras (ex.: deletar duas vezes, código especial, data no passado, discountValue abaixo do mínimo).
- [ ] H2 em memória.
- [ ] Repositório público no GitHub.
- [ ] Regras de negócio encapsuladas em **objetos de domínio** — domínio é diferente de entidade JPA. A entidade JPA é detalhe de persistência (infraestrutura); o domínio é um objeto Java puro com as regras.
- [ ] Docker + Docker Compose.
- [ ] Swagger / OpenAPI.
- [ ] Capricho sênior: commits pequenos e atômicos, mensagens de commit claras, PRs bem estruturados e revisáveis (mesmo trabalhando sozinho, estruturar como se fosse revisado por outro sênior), README com contexto/decisões, possivelmente CI simples (GitHub Actions rodando os testes).

## 7. Arquitetura — dicas de ouro (não é MVC simples)

O desafio pede explicitamente para **não** fazer só MVC + service genérico. Direção: arquitetura em camadas orientada a objeto / hexagonal (ports & adapters).

1. **Mate os services genéricos.** Nada de `CouponService` com 10 métodos. Cada intenção do usuário (ex.: `CreateCouponUseCase`, `DeleteCouponUseCase`) é uma classe própria, com uma única responsabilidade.
2. **Inversão de dependência.** A camada `application` nunca importa classes de `infra` (JPA/Hibernate/etc). Ela fala com o mundo externo através de interfaces (ports), implementadas na infraestrutura (adapters).
3. **UseCase é maestro, não músico.** O `UseCase`/`application` orquestra o fluxo; não contém `if/else` de regra de negócio. As regras (validações, transições de estado) vivem na entidade de domínio (`Coupon`, `CouponCode`, etc.), não no use case.
4. **Camada `application` agnóstica.** Não deve saber se roda na web, terminal ou fila — ou seja, sem `import org.springframework.web...` nem `import javax.persistence...` dentro de `application`.

**Checklist antes de cada commit** (por classe em `application`):
- Tem apenas um método público (geralmente `execute`)?
- Depende apenas de interfaces (ports)?
- Não tem import de Spring Web nem de JPA/Hibernate?

### Estrutura de pastas sugerida

```
src/main/java/.../coupon/
├── domain/              # Entidades, value objects, regras de negócio puras (sem framework)
│   ├── Coupon.java
│   ├── CouponCode.java
│   ├── CouponStatus.java
│   └── exceptions/
├── application/         # Use cases (orquestração) + ports (interfaces)
│   ├── usecase/
│   │   ├── CreateCouponUseCase.java
│   │   └── DeleteCouponUseCase.java
│   └── port/
│       ├── in/          # ex.: CreateCouponInputPort
│       └── out/         # ex.: CouponRepositoryPort
└── infrastructure/       # Adapters: web (controllers/DTOs), persistence (JPA), config
    ├── web/
    ├── persistence/
    └── config/
```

## 8. Fluxo de desenvolvimento (SDD)

Este projeto usa Spec-Driven Development via skills próprias em `.claude/skills/`:

1. **`/specify`** — transforma uma regra/requisito em `specs/<NN>-<slug>/spec.md` (regras numeradas e testáveis, contrato, casos de borda, critérios de aceite). Antes disso, consulta/propõe `specs/BACKLOG.md` com todas as US do projeto.
2. **`/plan`** — a partir da spec, gera `specs/<NN>-<slug>/plan.md` (domínio, ports, use case, adapters, testes previstos), respeitando a arquitetura da seção 7.
3. **`/tasks`** — quebra o plano em `specs/<NN>-<slug>/tasks.md`, uma lista ordenada de tarefas pequenas, cada uma = um commit atômico, e cria a branch `feat/US<NN>-<slug>`.
4. **`/implementar US<NN>-<slug>`** — implementa, tarefa por tarefa, tudo que está em `tasks.md`, rodando teste depois de cada uma, sem commitar. Termina com um changelog explicando cada decisão.
5. **`/audit`** — confere divergência entre `spec.md` e o código implementado (regra não implementada, implementada diferente do especificado, ou sem teste). Gera `specs/<NN>-<slug>/audit.md`.
6. **`/review-commit`** — só roda depois do `/audit` sem divergência pendente: checklist de arquitetura, sugestão de split em commits atômicos (um por tarefa) e mensagens no padrão Conventional Commits, antes de abrir PR.
7. **`/fechar-us`** — depois do PR mergeado em `develop`, reverifica na fonte (arquivos + git) que todas as etapas realmente aconteceram, e só então marca a US como `concluído` em `specs/BACKLOG.md`.

Nenhum código de produção deve ser escrito antes de existir spec + plano para a feature. Ver `specs/README.md` para a convenção de pastas.

## 9. Fluxo de branches e PRs

Três níveis, para deixar o histórico git legível e revisável (parte do capricho sênior do processo, não exigência formal do desafio):

```
feat/US001-create-coupon  ┐
feat/US002-delete-coupon  ┴─→  develop  ──→  main
```

- **`main`** — estado sempre entregável. Só recebe merge de `develop` via PR, nunca commit direto.
- **`develop`** — branch de integração. Recebe merge de cada `feat/USxxx-slug` via PR.
- **`feat/USxxx-slug`** — uma branch por spec em `specs/`. O número da US é o mesmo número da pasta da spec (`specs/001-create-coupon` → `feat/US001-create-coupon`), pra rastrear feature → branch → PR sem ambiguidade.

Regras:
1. Toda `feat/USxxx-*` nasce de `develop` atualizada (`git checkout develop && git pull && git checkout -b feat/US00X-slug`).
2. Dentro da branch, os commits seguem o `tasks.md` da spec (um commit por tarefa, `/review-commit` antes de cada um).
3. PR de `feat/USxxx-*` → `develop`: descrição referenciando a spec (`specs/00X-slug/spec.md`), o que mudou, como testar. Usa o template em `.github/pull_request_template.md`.
4. PR de `develop` → `main`: só depois que as features incluídas estiverem com testes passando — é o "corte de release".
5. Depois do merge em `develop`, a branch `feat/USxxx-*` pode ser apagada (local e remota).

> Dica: no GitHub, vale configurar *branch protection* em `main` e `develop` (Settings → Branches) pra exigir PR em vez de push direto — isso não dá pra automatizar por aqui sem um token de API, então é um passo manual seu.

## 10. Observações de processo

- Samuel cria o esqueleto do projeto manualmente no Spring Initializr — não gerar o projeto via automação.
- Ao desenvolver com IA, manter contexto completo do código no chat técnico de entrevista — evitar que a IA execute todo o fluxo sozinha sem o Samuel entender cada decisão (ele precisa defender o código numa conversa técnica depois).

## 11. Observability — decisão de escopo

Avaliamos adicionar uma trilha de auditoria completa (tabela de eventos, quem fez, quando, o quê mudou) — relevante em ecommerce de verdade (rastrear abuso de cupom, suporte ao cliente, compliance). Decisão: **não implementar como feature** neste desafio — é escopo que ninguém pediu, custa tempo e risco numa parte não avaliada, e o próprio desafio avisa que não é sobre criar endpoints extras.

Em vez disso, toda transição de estado relevante do domínio (cupom criado, cupom deletado) emite um **log estruturado leve**: SLF4J, nível INFO, com o identificador do cupom e o timestamp do evento — por exemplo `log.info("coupon {} deleted at {}", coupon.getId(), Instant.now())`, disparado no próprio método de domínio que faz a transição (`Coupon.delete()`, etc.), não espalhado pelo controller.

Isso é intencionalmente pouco: mostra o instinto de observability sem pagar o custo de uma feature não exigida. A auditoria completa (tabela de eventos, consulta por cupom/data) fica documentada aqui como "pensado, não implementado por escopo" — é um ponto de conversa pra entrevista, não um gap escondido.
