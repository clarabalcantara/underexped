# UnderExped: Sistema de Expedições Científicas Subterrâneas
> Enunciado do projeto no documento [TurmalinaPB](), pelo professor Frederico Costa

Disciplina **Programação para a Web III** (2026.2), feito por **Clara Alcântara** e **Artur Souza**.

## Sobre o projeto

Uma organização de pesquisa ambiental realiza expedições científicas em cavernas. Cada expedição reúne pesquisadores e guias, usa equipamentos patrimoniados, visita setores de uma caverna e produz coletas, amostras, documentos e um relatório final.

O objetivo do trabalho é **modelar esse domínio em Java e mapeá-lo para um banco PostgreSQL com Jakarta Persistence (JPA)**, tomando decisões justificadas sobre:

- classes, herança e objetos de valor;
- associações (1:1, 1:N, N:N), lado proprietário e cascatas;
- restrições do banco (obrigatoriedade, tamanho, unicidade, precisão);
- tipos de dados (enums, booleanos, binários, datas, valores monetários);
- carregamento de dados (LAZY/EAGER) e consultas sem o problema N+1.

As justificativas completas estão no **[RELATORIO.md](RELATORIO.md)**.

## Tecnologias

| Tecnologia | Versão | Para quê |
|---|---|---|
| Java | 21 | Linguagem |
| Jakarta Persistence (JPA) | 3.1 | Especificação do mapeamento objeto-relacional |
| Hibernate | 6.4.4 | Implementação da JPA |
| PostgreSQL | 16+ | Banco de dados |
| Lombok | 1.18 | Gera getters, setters e builders |
| Maven | — | Dependências e build |

## Como rodar

1. **Crie o banco** no PostgreSQL (pelo pgAdmin ou pelo terminal):
   ```sql
   CREATE DATABASE turmalina;
   ```
2. **Confira as credenciais** em `turmalina/src/main/resources/META-INF/persistence.xml`. O padrão é usuário `postgres` e senha `postgres`.
3. **Abra a pasta `turmalina/`** no IntelliJ como projeto Maven e aguarde o download das dependências.
4. **Ative o processamento de anotações** (necessário para o Lombok): *Settings → Build, Execution, Deployment → Compiler → Annotation Processors → Enable annotation processing*.
5. **Rode a classe `Main`**.

Ao rodar, o programa:
- **apaga e recria todas as tabelas** a partir das anotações das entidades (`drop-and-create`);
- grava dados de exemplo;
- executa as consultas do enunciado, imprimindo no console o SQL gerado e quantos comandos cada consulta executou.

Depois, as 15 tabelas e os dados podem ser conferidos no pgAdmin em **turmalina → Schemas → public → Tables**.

## Estrutura de pastas

```
underexped/
├── README.md                       ← este arquivo                  
├── .gitignore                      ← arquivos que o Git ignora (ex.: target/)
├── docs/
│   ├── diagrama-uml.md             ← fonte do diagrama de classes
│   ├──RELATORIO.md                 ← relatório técnico com as justificativas
│   └── diagrama-uml.png            ← diagrama de classes UML
└── turmalina/                      ← projeto Maven
    ├── pom.xml                     ← dependências (Hibernate, PostgreSQL, Lombok...)
    └── src/main/
        ├── java/br/com/turmalina/
        │   ├── Main.java           ← ponto de entrada: popula o banco e roda as consultas
        │   ├── model/              ← entidades (cada classe vira uma tabela)
        │   │   ├── enums/          ← enumerações
        │   │   ├── equipamento/    ← patrimônio
        │   │   └── pessoas/        ← hierarquia de pessoas (herança)
        │   ├── dto/                ← projeções usadas pelas consultas
        │   ├── queries/            ← classes com as consultas JPA
        │   └── utils/embeddables/  ← tipos incorporáveis (objetos de valor)
        └── resources/META-INF/
            ├── persistence.xml     ← configuração da conexão e do Hibernate
            └── orm.xml             ← consultas nomeadas
```

## O que cada arquivo faz

### `Main.java`

Ponto de entrada do programa. O método `popularBanco` grava um cenário de exemplo (uma caverna, dois setores, três pessoas, uma expedição planejada completa e uma expedição já concluída). Depois, o `main` executa as consultas e, após cada uma, imprime `>>> Comandos SQL executados: N`, que é a prova de que não há N+1.

### `model/`: entidades

Cada classe anotada com `@Entity` vira uma tabela no banco.

| Arquivo | Tabela | O que representa |
|---|---|---|
| `Caverna.java` | `caverna` | Caverna cadastrada, com código ambiental único e coordenadas |
| `Setor.java` | `setor` | Área de pesquisa dentro de uma caverna |
| `Expedicao.java` | `expedicao` | Entidade central: liga caverna, setores, plano, equipe e coletas |
| `PlanoSeguranca.java` | `plano_seguranca` | Plano obrigatório e exclusivo de cada expedição (1:1), com o mapa de rota |
| `AutorizacaoAmbiental.java` | `autorizacao_ambiental` | Autorização opcional da expedição (0..1), com o PDF assinado |
| `RelatorioFinal.java` | `relatorio_final` | Relatório opcional da expedição (0..1), com o arquivo completo |
| `Participacao.java` | `participacao` | Entidade associativa entre expedição e pessoa (papel, diária, presença) |
| `Coleta.java` | `coleta` | Coleta científica feita num setor, por um pesquisador |
| `Amostra.java` | `amostra` | Amostra gerada por uma coleta, com código de campo único e fotografia |

### `model/pessoas/`: herança

Herança com estratégia `JOINED`: dados comuns em `pessoa`, dados específicos em tabelas próprias.

| Arquivo | Tabela | O que representa |
|---|---|---|
| `Pessoa.java` | `pessoa` | Classe abstrata com nome, CPF único, e-mail, endereço... |
| `Pesquisador.java` | `pesquisador` | Registro institucional, área de pesquisa, titulação, bolsa |
| `GuiaEspeleologia.java` | `guia_espeleologia` | Credenciamento, nível e validade da certificação |

### `model/equipamento/`: patrimônio

| Arquivo | Tabela | O que representa |
|---|---|---|
| `Equipamento.java` | `equipamento` | Equipamento patrimoniado, com código patrimonial único |
| `MovimentacaoEquipamento.java` | `movimentacao_equipamento` | Entidade associativa: retirada e devolução de um equipamento numa expedição, por uma pessoa |

### `model/enums/`: enumerações

Gravadas no banco como **texto** (`@Enumerated(EnumType.STRING)`).

| Arquivo | Usado em |
|---|---|
| `SituacaoExpedicao` | Expedicao |
| `NivelDificuldade` | Setor |
| `TipoEquipamento`, `SituacaoOperacional` | Equipamento |
| `EstadoEquipamento` | MovimentacaoEquipamento |
| `PapelParticipante` | Participacao |
| `SituacaoValidacao` | Coleta |
| `CategoriaAmostra`, `CondicaoConservacao`, `UnidadeMedida` | Amostra |
| `SituacaoAutorizacao` | AutorizacaoAmbiental |
| `SituacaoRelatorio` | RelatorioFinal |

### `utils/embeddables/`: tipos incorporáveis

Objetos de valor sem identidade nem tabela própria: as colunas ficam dentro da tabela do dono.

| Arquivo | Dono | Colunas geradas |
|---|---|---|
| `Coordenada.java` | `Caverna` | `latitude`, `longitude`, `datum_geodesico` |
| `Endereco.java` | `Pessoa` | `logradouro`, `numero`, `complemento`, `bairro`, `cidade`, `uf`, `cep` |

### `dto/`: projeções

Resumos imutáveis (`record`) com só os campos que uma consulta precisa mostrar. Evitam carregar entidades inteiras e binários.

| Arquivo | Usado na consulta | Campos |
|---|---|---|
| `ExpedicaoResumo.java` | 1 | código, título, caverna, datas, situação |
| `ParticipanteResumo.java` | 2 (alternativa) | nome, papel, presença |
| `AmostraResumo.java` | 4 | dados da amostra **sem** a fotografia |

### `queries/`: consultas

Classes que sabem buscar dados no banco. O `Main` só chama os métodos delas.

| Arquivo | Métodos | Consultas do enunciado |
|---|---|---|
| `ExpedicaoConsultas.java` | `listarPorPeriodoESituacao`, `buscarDetalhes`, `listarParticipantes`, `listarColetas`, `listarAmostras` | 1 a 4 |
| *(em implementação)* | equipamentos disponíveis e downloads de arquivos | 5 e 6 |

### `resources/META-INF/`

| Arquivo | Função |
|---|---|
| `persistence.xml` | Define a unidade de persistência `turmalina`: conexão com o PostgreSQL, `drop-and-create` e exibição do SQL |
| `orm.xml` | Consultas nomeadas (pedido do cliente): `Expedicao.listarPorPeriodoESituacao` e `Coleta.listarPorExpedicao` |

## Consultas implementadas

| # | Consulta | Comandos SQL |
|---|---|---|
| 1 | Expedições por período e situação | 1 |
| 2 | Detalhes da expedição com participantes, sem binários | 2 |
| 3 | Coletas com setor e pesquisador | 1 |
| 4 | Amostras de uma coleta, só quando aberta | 1 |
| 5 | Equipamentos disponíveis numa faixa de datas | *em implementação* |
| 6 | Download separado do mapa, da autorização e do relatório | *em implementação* |
