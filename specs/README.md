# Convenção de specs

Cada feature vive em uma pasta numerada: `specs/<NN>-<slug>/`, por exemplo `specs/001-create-coupon/`.

Dentro de cada pasta, até três arquivos, gerados nesta ordem pelo fluxo SDD do projeto:

1. `spec.md` — gerado pela skill `/specify`: regras de negócio numeradas, contrato da API, casos de borda, critérios de aceite.
2. `plan.md` — gerado pela skill `/plan`: domínio, ports, use case, adapters, testes previstos.
3. `tasks.md` — gerado pela skill `/tasks`: checklist de tarefas pequenas, cada uma = um commit.

Fluxo completo: **`/specify` → `/plan` → `/tasks` → criar branch `feat/US<NN>-<slug>` a partir de `develop` → (implementar tarefa por tarefa) → `/review-commit` antes de cada commit → PR para `develop`.**

O número da pasta (`<NN>`) é o mesmo número da branch (`specs/001-create-coupon` → `feat/US001-create-coupon`), pra rastrear spec → branch → PR sem ambiguidade. Ver seção 9 do `CLAUDE.md` para o fluxo de branches completo (`feat/USxxx` → `develop` → `main`).

Nenhuma dessas etapas deve ser pulada só porque "a feature é simples" — é justamente o ponto deste desafio: parece CRUD simples, mas é avaliado pelo rigor das regras de negócio.
