## Spec relacionada

`specs/<NN>-<slug>/spec.md`

## O que mudou

<!-- Resumo curto, em uma ou duas frases. Se o PR mistura mais de uma coisa, considere quebrar em PRs menores. -->

## Como testar

<!-- Comandos ou passos para rodar localmente, ex: ./mvnw test -Dtest=... -->

## Checklist

- [ ] Regras de negócio da spec cobertas por teste (uma por regra numerada)
- [ ] Casos de borda da spec também testados (não só o caminho feliz)
- [ ] `application/` não importa `org.springframework.web...` nem `jakarta.persistence...`
- [ ] Regra de negócio está na entidade de domínio, não no use case ou no controller
- [ ] `./mvnw clean verify` passando localmente
