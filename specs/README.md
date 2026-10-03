# Convenção de specs

Cada feature vive em uma pasta numerada: `specs/<NN>-<slug>/`, por exemplo `specs/001-create-coupon/`.

Dentro de cada pasta, até três arquivos, gerados nesta ordem pelo fluxo SDD do projeto:

1. `spec.md` — gerado pela skill `/specify`: regras de negócio numeradas, contrato da API, casos de borda, critérios de aceite.
2. `plan.md` — gerado pela skill `/plan`: domínio, ports, use case, adapters, testes previstos.
3. `tasks.md` — gerado pela skill `/tasks`: checklist de tarefas pequenas, cada uma = um commit.

Fluxo completo: **`/specify` → `/plan` → `/tasks` → (implementar tarefa por tarefa) → `/review-commit` antes de cada commit.**

Nenhuma dessas etapas deve ser pulada só porque "a feature é simples" — é justamente o ponto deste desafio: parece CRUD simples, mas é avaliado pelo rigor das regras de negócio.
