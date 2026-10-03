# Convenção de specs

`BACKLOG.md` é a fonte da verdade de quantas User Stories (US) o projeto todo tem e qual o status de cada uma — consulte-o antes de criar uma spec nova, em vez de perguntar/assumir um número.

Cada feature vive em uma pasta numerada: `specs/<NN>-<slug>/`, por exemplo `specs/001-create-coupon/`.

Dentro de cada pasta, até três arquivos, gerados nesta ordem pelo fluxo SDD do projeto:

1. `spec.md` — gerado pela skill `/specify`: regras de negócio numeradas, contrato da API, casos de borda, critérios de aceite.
2. `plan.md` — gerado pela skill `/plan`: domínio, ports, use case, adapters, testes previstos.
3. `tasks.md` — gerado pela skill `/tasks`: checklist de tarefas pequenas, cada uma = um commit.
4. `audit.md` — gerado pela skill `/audit`: matriz regra ↔ implementação ↔ teste, com qualquer divergência encontrada.

Fluxo completo: **`/specify` → `/plan` → `/tasks` (cria a branch `feat/US<NN>-<slug>`) → `/implementar US<NN>-<slug>` → `/audit` → `/review-commit` → PR para `develop`.**

O número da pasta (`<NN>`) é o mesmo número da branch (`specs/001-create-coupon` → `feat/US001-create-coupon`), pra rastrear spec → branch → PR sem ambiguidade. Ver seção 9 do `CLAUDE.md` para o fluxo de branches completo (`feat/USxxx` → `develop` → `main`).

Nenhuma dessas etapas deve ser pulada só porque "a feature é simples" — é justamente o ponto deste desafio: parece CRUD simples, mas é avaliado pelo rigor das regras de negócio.
