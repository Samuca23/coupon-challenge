# Backlog de Specs (User Stories)

Fonte da verdade de quantas US o projeto tem, números/slugs reservados e status de cada uma. A skill `/specify` consulta este arquivo antes de perguntar "qual feature" — ver seção 9 do `CLAUDE.md` para o fluxo de branches correspondente.

| US | Slug | Branch | Status | Descrição |
|---|---|---|---|---|
| US001 | create-coupon | feat/US001-create-coupon | planejado | Criar cupom com todas as regras de validação (code normalizado a 6 chars, discountValue ≥ 0,5, expirationDate não pode ser passado, published opcional, status ACTIVE) |
| US002 | delete-coupon | feat/US002-delete-coupon | planejado | Soft delete de cupom (transição de status para DELETED), incluindo a regra de não permitir deletar um cupom já deletado |

Status possíveis: `planejado` → `especificado` (spec.md pronto) → `planejado-tecnicamente` (plan.md pronto) → `em desenvolvimento` (branch ativa, tasks.md em andamento) → `concluído` (PR mergeado em `develop`).

## Fora do backlog (chores, sem regra de negócio — não viram US)

Itens de entrega do desafio que não têm regra de negócio para especificar, tratados como commits/PRs diretos (ver seção 6 do `CLAUDE.md`): Dockerfile + docker-compose.yml, README.md do projeto, metadata do OpenAPI/Swagger, configuração de cobertura de teste (JaCoCo).

Revise esta lista sempre que surgir uma regra de negócio nova que não seja apenas uma tarefa dentro de uma US já existente — não quando surgir só uma tarefa de infraestrutura.
