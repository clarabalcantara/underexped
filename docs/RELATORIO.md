# Relatório – Projeto TurmalinaPB

Java 21, JPA 3.1 (Hibernate 6.4.4), PostgreSQL e Lombok.

## 1. Modelo

Mapeamos 14 entidades, o que cobre tudo que o enunciado descreve:

- cadastro: `Caverna`, `Setor`
- pessoas: `Pessoa` (abstrata), `Pesquisador`, `GuiaEspeleologia`
- expedição: `Expedicao`, `PlanoSeguranca`, `AutorizacaoAmbiental`, `RelatorioFinal`, `Participacao`
- equipamentos: `Equipamento`, `MovimentacaoEquipamento`
- coletas: `Coleta`, `Amostra`

Também temos 2 tipos incorporáveis (`Coordenada` e `Endereco`) e 12 enums. No banco ficam 15 tabelas: as 14 entidades mais `expedicao_setor`. O diagrama UML está em `docs/`.

<img width="707" height="441" alt="image" src="https://github.com/user-attachments/assets/4fa36653-dc9e-471a-9321-a4c191c287de" />


Algumas decisões ligadas às regras de negócio:

- O responsável pela coleta é do tipo `Pesquisador`, e não `Pessoa`, porque o enunciado diz que a coleta é conduzida por um pesquisador. Assim um guia não pode ser responsável.
- Entendemos que cada expedição tem no máximo uma autorização (FK `expedicao_id` com `unique`). Se fosse preciso guardar autorizações vencidas, teríamos que trocar por `@ManyToOne` com um índice único parcial, que a JPA não gera.
- Regras que o banco não consegue garantir ficam em `@PrePersist`/`@PreUpdate`: término depois do início, limite de participantes, setores da mesma caverna da expedição e validade da autorização depois da emissão.

## 2. IDs e restrições

Todas as entidades usam `@GeneratedValue(strategy = GenerationType.IDENTITY)`, que no PostgreSQL vira `id bigserial`. No começo algumas classes estavam com `AUTO`, e o Hibernate criava sequences separadas (`caverna_SEQ`, incrementando de 50 em 50). Trocamos para `IDENTITY` para ficar igual em todas.

<img width="543" height="373" alt="image" src="https://github.com/user-attachments/assets/c13dc3c0-0f6e-4f38-94a3-9b2f9cf08709" />

As restrições estão nas anotações:

- `nullable = false` → `not null`
- `length` → tamanho do `varchar` (nome 150, e-mail 120, CPF 11, UF 2...)
- `unique = true` → CPF, código ambiental, código da expedição, código patrimonial e código de campo da amostra
- `precision` e `scale` → valores em `numeric`, ex.: `orcamento_aprovado numeric(12,2)`
- `@UniqueConstraint(expedicao_id, pessoa_id)` em `Participacao`, para a mesma pessoa não entrar duas vezes na mesma expedição

## 3. Herança e incorporáveis

Usamos herança `JOINED`. A tabela `pessoa` tem os dados comuns e `pesquisador` e `guia_espeleologia` têm só os específicos, com o mesmo `id`. Por isso gravar um pesquisador faz dois inserts, um em `pessoa` e outro em `pesquisador`.

<img width="1349" height="662" alt="image" src="https://github.com/user-attachments/assets/05c33f06-7003-40b1-8826-9e5f0934703e" />


Escolhemos `JOINED` porque:

- com `SINGLE_TABLE` os campos específicos teriam que aceitar `null` (um guia não tem titulação, por exemplo);
- com `TABLE_PER_CLASS` o CPF ficaria em tabelas diferentes e não daria para garantir que é único, e também não teria uma tabela `pessoa` para a `Participacao` apontar;
- o enunciado diz que podem surgir novos tipos de pessoa, e com `JOINED` é só criar uma tabela nova.

Colocamos `@DiscriminatorColumn(name = "tipo_pessoa")` para ver o tipo de cada pessoa direto na tabela (`PESQUISADOR` ou `GUIA`).

`Coordenada` e `Endereco` são `@Embeddable`: não têm id nem tabela, as colunas ficam dentro de `caverna` e de `pessoa`. Latitude e longitude são `numeric(9,6)`.

## 4. Tipos de dados

<img width="1317" height="662" alt="image" src="https://github.com/user-attachments/assets/8e9fad35-5b56-4381-ac7c-5f2c035a9fde" />


- **Enums:** todos com `@Enumerated(EnumType.STRING)`, então o banco guarda o texto (`PLANEJADA`). Com o padrão (`ORDINAL`) guardaria 0, 1, 2, e mudar a ordem do enum estragaria os dados. Os enums do cadastro estavam sem isso no início e geravam `smallint`; corrigimos.
- **Booleanos:** `boolean`, que vira `boolean` no PostgreSQL.
- **Binários:** `@Lob byte[]` no mapa da rota, no PDF da autorização, no arquivo do relatório e na foto da amostra. No PostgreSQL vira `oid`, e por isso só dá para ler esses campos dentro de uma transação.
- **Datas:** `LocalDate` para datas sem hora; `LocalDateTime` para início e término previstos da expedição (horário de agenda); `Instant` para eventos, como a hora da coleta e a retirada de equipamento.
- **Números:** `BigDecimal` em dinheiro e medições, para não ter erro de arredondamento do `double`.

## 5. Associações, cascata e orphanRemoval

<img width="864" height="615" alt="image" src="https://github.com/user-attachments/assets/e2642990-6fa6-4874-9fab-488eda2fdac3" />


O lado dono de cada associação é o que tem a FK. Só mapeamos o lado inverso (`mappedBy`) quando a gente realmente navega por ele: `Caverna.setores`, `Expedicao.participacoes`, `Expedicao.coletas`, `Coleta.amostras` e `PlanoSeguranca.expedicao`.

No 1:1 do plano de segurança, a FK fica em `expedicao` (`plano_seguranca_id not null unique`). Assim o banco garante que toda expedição tem plano, e o plano pode ser LAZY (no Hibernate só o lado dono do 1:1 fica LAZY sem configuração extra).

Usamos `cascade = ALL` e `orphanRemoval = true` só onde o filho não existe sem o pai:

- `Caverna.setores`
- `Expedicao.planoSeguranca`, `participacoes` e `coletas`
- `Coleta.amostras`

Caverna, pessoas, equipamentos, autorização, relatório e movimentações não têm cascata, porque têm vida própria ou são registros que não podem sumir junto com a expedição. No `Main`, um `persist(expedicao)` salva plano, participações, coletas e amostras de uma vez; autorização e relatório precisam de `persist` próprio.

## 6. Carregamento e consultas

Todos os `@ManyToOne` e `@OneToOne` estão com `fetch = FetchType.LAZY` (o padrão deles é EAGER). Quando uma consulta precisa de alguma associação, ela busca com `join fetch` ou usa um DTO com só os campos necessários.

| # | Consulta | Como | Comandos SQL |
|---|---|---|---|
| 1 | Expedições por período e situação | DTO `ExpedicaoResumo`, nomeada no `orm.xml` | 1 |
| 2 | Detalhes da expedição com participantes | `join fetch` + uma segunda consulta para os setores | 2 |
| 2b | Só nome e papel dos participantes | DTO `ParticipanteResumo` | 1 |
| 3 | Coletas com setor e pesquisador | `join fetch`, nomeada no `orm.xml` | 1 |
| 4 | Amostras de uma coleta | DTO `AmostraResumo`, sem a foto | 1 |
| 5 | Equipamentos disponíveis num período | em andamento | – |
| 6 | Baixar mapa, PDF ou relatório | em andamento | – |

Os números foram contados com `hibernate.generate_statistics`. Alguns pontos:

- A consulta 1 traz só as 6 colunas da listagem. Nos dados de teste há uma expedição concluída, e ela não aparece, o que mostra o filtro funcionando.
- Na consulta 2, os setores vêm numa segunda consulta porque buscar participantes e setores no mesmo select multiplicaria as linhas. São sempre 2 comandos, não importa quantos participantes.
- Nenhuma das consultas 1 a 4 lê colunas binárias.
- A consulta 3 dava 2 comandos no começo: o segundo era um `select` na tabela `caverna`, porque `Setor.caverna` ainda estava EAGER. Depois de colocar LAZY caiu para 1. Com 10 cavernas seriam 11 comandos, que é o problema N+1.

Exemplo do SQL da consulta 1:

```sql
select e1_0.codigo, e1_0.titulo, c1_0.nome_oficial,
       e1_0.inicio_previsto, e1_0.termino_previsto, e1_0.situacao
from expedicao e1_0
join caverna c1_0 on c1_0.id = e1_0.caverna_id
where e1_0.inicio_previsto >= ? and e1_0.inicio_previsto < ? and e1_0.situacao = ?
order by e1_0.inicio_previsto
```

Para as consultas 5 e 6 a ideia é:

- **5:** um `not exists` que descarta equipamentos com movimentação no período, sem carregar o histórico. `Equipamento` nem tem a lista de movimentações mapeada.
- **6:** uma consulta por arquivo trazendo só a coluna dele, ex.: `select a.pdfAssinado from AutorizacaoAmbiental a where a.expedicao.id = :id`.

As consultas 1 e 3 estão no `orm.xml` como consultas nomeadas, como o cliente pediu, e são chamadas com `createNamedQuery`.

## 7. Organização do código

As entidades estão em `model` (com subpacotes para enums, pessoas e equipamentos), os incorporáveis em `utils/embeddables`, os DTOs em `dto` e as consultas em `queries`. O `Main` cria dados de exemplo e roda as consultas mostrando o SQL. A explicação de cada pasta está no `README.md`.

## Observações

**Relacionamentos.** No 1:1 a FK tem `unique` (plano, autorização e relatório). No 1:N a FK fica no lado "muitos" (ex.: `setor.caverna_id`). No N:N sem dados extras usamos `@ManyToMany` com tabela de junção (`expedicao_setor`); quando a ligação tem dados próprios virou entidade (`Participacao` e `MovimentacaoEquipamento`).

**LAZY e EAGER.** EAGER busca a associação sempre; LAZY só quando o código acessa. Deixamos tudo LAZY e buscamos o necessário em cada consulta. O único EAGER que sobra é o lado inverso `PlanoSeguranca.expedicao`, que o Hibernate não deixa ser LAZY sem enhancement, mas nenhuma consulta carrega o plano.

**Persistência.** O `persistence.xml` define a unidade `turmalina`, a conexão com o PostgreSQL e o `drop-and-create`, que recria as tabelas toda vez que o programa roda. As tabelas não são criadas à mão: o Hibernate gera a partir das anotações. No código, o `EntityManager` faz os `persist` e as consultas dentro de uma transação, e o `em.clear()` no `Main` limpa a memória do Hibernate entre uma consulta e outra para a contagem ficar correta.

## Pendências

- consultas 5 e 6
- prints do pgAdmin
- confirmar com o professor se pode ser em dupla
