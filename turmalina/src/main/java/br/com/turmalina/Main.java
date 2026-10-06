package br.com.turmalina;

import br.com.turmalina.dto.AmostraResumo;
import br.com.turmalina.dto.ExpedicaoResumo;
import br.com.turmalina.dto.ParticipanteResumo;
import br.com.turmalina.model.*;
import br.com.turmalina.utils.embeddables.*;
import br.com.turmalina.model.enums.*;
import br.com.turmalina.model.pessoas.GuiaEspeleologia;
import br.com.turmalina.model.pessoas.Pesquisador;
import br.com.turmalina.queries.ExpedicaoConsultas;
import br.com.turmalina.queries.EquipamentoConsultas;
import br.com.turmalina.queries.ArquivoConsultas;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.NoResultException;
import jakarta.persistence.Persistence;
import org.hibernate.SessionFactory;
import org.hibernate.stat.Statistics;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Clara Alcântara e Artur Souza
 * Programação para Web III
 * Cria o banco, grava dados de exemplo e roda as consultas do enunciado,
 * mostrando o SQL gerado e quantos comandos cada consulta executou.
 */
public class Main {

    private static final String UNIDADE = "turmalina";

    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory(UNIDADE);
        Statistics stats = emf.unwrap(SessionFactory.class).getStatistics();
        EntityManager em = emf.createEntityManager();

        ExpedicaoConsultas consultas = new ExpedicaoConsultas(em);
        EquipamentoConsultas equipamentoConsultas = new EquipamentoConsultas(em);
        ArquivoConsultas arquivoConsultas = new ArquivoConsultas(em);

        // Long expedicaoId = popularBanco(emf);

        List<Expedicao> expedicoes = consultas.getAllExpedicoes();
        Expedicao expedicao = expedicoes.get(2);
        Long expedicaoId = expedicao.getId();
        
        // consulta 1
        titulo("CONSULTA 1: expedições planejadas em 2026", stats);
        List<ExpedicaoResumo> lista = consultas.listarPorPeriodoESituacao(
                LocalDateTime.of(2026, 1, 1, 0, 0),
                LocalDateTime.of(2027, 1, 1, 0, 0),
                SituacaoExpedicao.PLANEJADA);
        lista.forEach(r -> System.out.println("  " + r));
        // comandos(stats);

        // consulta 2
        titulo("CONSULTA 2: detalhes da expedição com participantes", stats);
        Expedicao e = consultas.buscarDetalhes(expedicaoId);
        System.out.println("  " + e.getCodigo() + " em " + e.getCaverna().getNomeOficial());
        e.getParticipacoes().forEach(p ->
                System.out.println("  - " + p.getPessoa().getNome() + " (" + p.getPapel() + ")"));
        e.getSetores().forEach(s -> System.out.println("  setor: " + s.getDenominacao()));
        // comandos(stats);

        // consulta 2, só nome e papel dos participantes
        // em.clear();
        titulo("CONSULTA 2 (alternativa): participantes por projeção", stats);
        List<ParticipanteResumo> participantes = consultas.listarParticipantes(expedicaoId);
        participantes.forEach(p -> System.out.println("  " + p));
        // comandos(stats);

        // consulta 3
        // em.clear();
        titulo("CONSULTA 3: coletas com setor e pesquisador", stats);
        List<Coleta> coletas = consultas.listarColetas(expedicaoId);
        for (Coleta c : coletas) {
            System.out.println("  " + c.getMetodo() + " | setor: " + c.getSetor().getDenominacao()
                    + " | responsável: " + c.getResponsavel().getNome());
        }
        // comandos(stats);

        // consulta 4
        // em.clear();
        titulo("CONSULTA 4: amostras da primeira coleta", stats);
        List<AmostraResumo> amostras = consultas.listarAmostras(expedicaoId);

        System.out.println(expedicao.getTitulo() + "Não possui amostras.");
        amostras.forEach(a -> System.out.println("  " + a));
        // comandos(stats);

        // consultas 5 e 6 entram aqui

        // consulta 5
        // em.clear();
        titulo("CONSULTA 5: Listagem de equipamentos por faixa de data", stats);
        List<Equipamento> equipamentos = equipamentoConsultas.getEquipamentosDisponiveisEntreFaixas(Instant.parse("2026-10-05T22:15:30-03:00"), Instant.parse("2026-10-19T22:15:30-03:00"));
        // 2026-10-05T22:15:30-03:00
        for (Equipamento tool : equipamentos) {
            System.out.println(tool.getNome());
        }
        // comandos(stats);

        // consulta 6
        // 6.1 Mapa segurança
        titulo("CONSULTA 6.1: Mapa de segurança", stats);
        byte[] mapaSeg = arquivoConsultas.getMapaSegurancaByExpedicao(expedicao);
        System.out.println(mapaSeg);

        // 6.2 Autorização ambiental
        titulo("CONSULTA 6.2: Autorizaçao Ambiental", stats);
        AutorizacaoAmbiental autorizacaoAmbiental;
        try {
                autorizacaoAmbiental = arquivoConsultas.getAutorizacaoAmbientalByExpedicao(expedicao);
                System.out.println(autorizacaoAmbiental);

        } catch (NoResultException e1) {
                System.out.println("Expedição não possui Autorizaçao Ambiental");
        }

        // 6.3 Relatorio Final
        titulo("CONSULTA 6.3: Relatório Final", stats);
        RelatorioFinal relatorioFinal;
        try {
                relatorioFinal = arquivoConsultas.getRelatorioFinalByExpedicao(expedicao);
                System.out.println(relatorioFinal);

        } catch (NoResultException e2) {
                System.out.println("Expedição não possui relatório");
        }

        if (emf.isOpen()) {
            emf.close();
        }
    }

    /**
     * Grava os dados de exemplo e devolve o id da expedição principal.
     * A EXP-002 já está concluída, então não deve aparecer na consulta 1.
     */
    private static Long popularBanco(EntityManagerFactory emf) {
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();

        // caverna e setores
        Caverna caverna = Caverna.builder()
                .nomeOficial("Gruta XYZ")
                .codigoAmbiental("CAV-001")
                .municipio("Boa Vista")
                .uf("PB")
                .acessoPermitido(true)
                .build();
        Setor setorA = Setor.builder().denominacao("Setor A")
                .nivelDificuldade(NivelDificuldade.MODERADO).condicaoCorrente("Seco")
                .caverna(caverna).build();
        Setor setorB = Setor.builder().denominacao("Setor B")
                .nivelDificuldade(NivelDificuldade.ALTO).condicaoCorrente("Úmido")
                .riscoInundacao(true)
                .caverna(caverna).build();


        // pessoas: dois pesquisadores e uma guia
        Pesquisador maria = Pesquisador.builder()
                .nome("Maria Silva").cpf("11111111111").ativo(true)
                .dataNascimento(LocalDate.of(1990, 1, 10)).email("maria@email.com")
                .registroInstitucional("PQ-01").areaPesquisa("Geologia")
                .titulacao("Doutora").valorDiarioBolsa(new BigDecimal("150.00")).build();
        Pesquisador joao = Pesquisador.builder()
                .nome("João Santos").cpf("22222222222").ativo(true)
                .dataNascimento(LocalDate.of(1995, 5, 20)).email("joao@email.com")
                .registroInstitucional("PQ-02").areaPesquisa("Biologia")
                .titulacao("Mestre").valorDiarioBolsa(new BigDecimal("120.00")).build();
        GuiaEspeleologia ana = GuiaEspeleologia.builder()
                .nome("Ana Costa").cpf("33333333333").ativo(true)
                .dataNascimento(LocalDate.of(1988, 3, 15)).email("ana@email.com")
                .numeroCredenciamento("G-01").nivelCertificacao("Avançado")
                .validadeCertificacao(LocalDate.of(2027, 12, 31)).expedicoesConcluidas(10).build();

        em.persist(caverna);
        em.persist(setorA);
        em.persist(setorB);
        em.persist(maria);
        em.persist(joao);
        em.persist(ana);


        // expedição principal
        PlanoSeguranca plano = new PlanoSeguranca(
                "Sair pela entrada principal.", "Entrada", 60, "83999999999", true);
        plano.setMapaRota(new byte[]{1, 2, 3});

        

        Expedicao expedicao = Expedicao.builder()
            .codigo("EXP-001")
            .titulo("Expedição Teste")
            .objetivo("Mapear o setor B.")
            .inicioPrevisto(LocalDateTime.of(2026, 11, 3, 8, 0))
            .terminoPrevisto(LocalDateTime.of(2026, 11, 5, 17, 0))
            .orcamentoAprovado(new BigDecimal("10000.00"))
            .maxParticipantes(5)
            .caverna(caverna)
            .planoSeguranca(plano)
            .build(); // O Lombok vai inicializar o Set de setores aqui automaticamente

        expedicao.adicionarSetor(setorA);
        expedicao.adicionarSetor(setorB);

        

        expedicao.adicionarParticipacao(
                new Participacao(maria, PapelParticipante.COORDENADOR, new BigDecimal("200.00"), 3));
        expedicao.adicionarParticipacao(
                new Participacao(joao, PapelParticipante.PESQUISADOR, new BigDecimal("150.00"), 3));
        expedicao.adicionarParticipacao(
                new Participacao(ana, PapelParticipante.GUIA, new BigDecimal("180.00"), 3));

                

        Coleta coleta1 = new Coleta(setorB, maria, Instant.parse("2026-11-04T10:00:00Z"),
                "Coleta manual", "Ponto 1");
        coleta1.setTemperatura(new BigDecimal("18.50"));
        coleta1.adicionarAmostra(new Amostra("AM-001", CategoriaAmostra.AGUA,
                new BigDecimal("500"), UnidadeMedida.ML, LocalDate.of(2026, 11, 4),
                CondicaoConservacao.INTACTA, false));
        coleta1.adicionarAmostra(new Amostra("AM-002", CategoriaAmostra.SEDIMENTO,
                new BigDecimal("50"), UnidadeMedida.G, LocalDate.of(2026, 11, 4),
                CondicaoConservacao.INTACTA, false));

        Coleta coleta2 = new Coleta(setorA, joao, Instant.parse("2026-11-04T15:00:00Z"),
                "Armadilha", "Ponto 2");
        coleta2.adicionarAmostra(new Amostra("AM-003", CategoriaAmostra.BIOLOGICA,
                new BigDecimal("2.5"), UnidadeMedida.G, LocalDate.of(2026, 11, 4),
                CondicaoConservacao.INTACTA, true));

        expedicao.adicionarColeta(coleta1);
        expedicao.adicionarColeta(coleta2);

        

        // salva a expedição junto com plano, participações, coletas e amostras (cascade)
        em.persist(expedicao);

        AutorizacaoAmbiental autorizacao = new AutorizacaoAmbiental(expedicao, "AUT-001",
                "ICMBio", LocalDate.of(2026, 9, 1), LocalDate.of(2027, 9, 1));
        autorizacao.setSituacao(SituacaoAutorizacao.VIGENTE);
        autorizacao.setPdfAssinado(new byte[]{4, 5, 6});
        em.persist(autorizacao);

        

        RelatorioFinal relatorio = new RelatorioFinal(expedicao, "Relatório da Expedição Teste",
                "Resumo da expedição.", LocalDate.of(2026, 11, 20), 10);
        relatorio.setArquivo(new byte[]{7, 8, 9});
        em.persist(relatorio);

        

        // expedição antiga, já concluída
        PlanoSeguranca plano2 = new PlanoSeguranca(
                "Sair pela entrada principal.", "Entrada", 30, "83999999999", false);

        Expedicao antiga = Expedicao.builder()
            .codigo("EXP-002")
            .titulo("Expedição Antiga")
            .objetivo("Vistoria")
            .inicioPrevisto(LocalDateTime.of(2026, 9, 10, 8, 0))
            .terminoPrevisto(LocalDateTime.of(2026, 9, 10, 16, 0))
            .orcamentoAprovado(new BigDecimal("2000.00"))
            .maxParticipantes(5)
            .caverna(caverna)
            .planoSeguranca(plano2)
            .build();

        antiga.adicionarSetor(setorA);
        antiga.setSituacao(SituacaoExpedicao.CONCLUIDA);
        em.persist(antiga);



        Endereco endereco = Endereco.builder()
                .logradouro("Rua das Palmeiras")
                .numero("123")
                .bairro("Centro")
                .cidade("Bonito")
                .uf("MS")
                .cep("79290000")
                .build();

        // 2. Criar Pessoas (Subclasses de Pessoa)
        // Responsável pela movimentação (Arnaldo)
        GuiaEspeleologia arnaldo = GuiaEspeleologia.builder()
                .nome("Arnaldo Silva")
                .cpf("12345678900")
                .dataNascimento(LocalDate.of(1985, 5, 20))
                .email("arnaldo@email.com")
                .telefone("67999991111")
                .ativo(true)
                .endereco(endereco)
                .numeroCredenciamento("GUIA-MS-5432")
                .nivelCertificacao("Avançado")
                .validadeCertificacao(LocalDate.now().plusYears(2))
                .expedicoesConcluidas(15)
                .build();
        em.persist(arnaldo);

        // Participante da Expedição (Beatriz)
        Pesquisador beatriz = Pesquisador.builder()
                .nome("Beatriz Souza")
                .cpf("98765432111")
                .dataNascimento(LocalDate.of(1990, 8, 12))
                .email("beatriz@universidade.edu")
                .telefone("67988882222")
                .ativo(true)
                .endereco(endereco)
                .registroInstitucional("REQ-2026-X")
                .areaPesquisa("Geologia de Cavernas")
                .titulacao("Doutorado")
                .valorDiarioBolsa(new BigDecimal("150.00"))
                .build();
        em.persist(beatriz);

        // 3. Criar Coordenada (Embeddable) e Caverna
        Coordenada coordenadaCaverna = Coordenada.builder()
                .latitude(new BigDecimal("-21.123456"))
                .longitude(new BigDecimal("-56.654321"))
                .datumGeodesico("WGS84")
                .build();

        Caverna caverna2 = Caverna.builder()
                .nomeOficial("Gruta do Lago Azul")
                .codigoAmbiental("CAV-MS-001")
                .municipio("Bonito")
                .uf("MS")
                .altitude(new BigDecimal("350.00"))
                .extensaoConhecida(new BigDecimal("1200.50"))
                .dataUltimaInspecao(LocalDate.now().minusMonths(1))
                .acessoPermitido(true)
                .coordenada(coordenadaCaverna)
                .build();
        em.persist(caverna2);

        // 4. Criar Plano de Segurança
        PlanoSeguranca planoSeguranca = PlanoSeguranca.builder()
                .procedimentosEvacuacao("Rota de fuga principal pela entrada norte.")
                .pontoEncontro("Estacionamento principal da reserva.")
                .tempoMaxSemComunicacaoMin(120)
                .telefoneEmergencia("(67) 193")
                .exigeEquipeMedica(false)
                .mapaRota(new byte[]{0, 1, 0, 1}) // Exemplo de array de bytes
                .build();
        em.persist(planoSeguranca);

        // 5. Criar a Expedição
        Expedicao expedicao2 = Expedicao.builder()
                .codigo("EXP-2026-09")
                .titulo("Mapeamento Geológico Trimestral")
                .objetivo("Análise estrutural dos salões internos.")
                .inicioPrevisto(LocalDateTime.now().plusDays(2))
                .terminoPrevisto(LocalDateTime.now().plusDays(5))
                .orcamentoAprovado(new BigDecimal("5000.00"))
                .custoRealizado(BigDecimal.ZERO)
                .maxParticipantes(5)
                .situacao(SituacaoExpedicao.PLANEJADA)
                .cancelamentoEmergencial(false)
                .caverna(caverna2)
                .planoSeguranca(planoSeguranca)
                .build();
        em.persist(expedicao2);

        // 6. Criar Participação de Pessoa na Expedição
        Participacao participacao = Participacao.builder()
                .papel(PapelParticipante.PESQUISADOR)
                .dataConfirmacao(LocalDate.now())
                .valorDiaria(new BigDecimal("200.00"))
                .diasPrevistos(3)
                .presencaConfirmada(true)
                .observacoes("Necessita levar amostradores esterilizados.")
                .pessoa(beatriz)
                .expedicao(expedicao2)
                .build();
        em.persist(participacao);

        // 7. Criar o Equipamento
        Equipamento equipamento = Equipamento.builder()
                .codigoPatrimonial("EQ001")
                .nome("Capacete Petzl Vertex")
                .tipo(TipoEquipamento.CAPACETE)
                .fabricante("Petzl")
                .valorAquisicao(new BigDecimal("1000.00"))
                .dataCompra(LocalDate.now())
                .ultimaManutencao(LocalDate.now())
                .situacao(SituacaoOperacional.OPERANTE)
                .exigeCalibracao(false)
                .build();
        em.persist(equipamento);

        // 8. Criar a Movimentação do Equipamento associada à Expedição e ao Responsável
        MovimentacaoEquipamento movimento = MovimentacaoEquipamento.builder()
                .retirada(Instant.now())
                .previsaoDevolucao(Instant.now().plusSeconds(3600 * 24 * 5)) // 5 dias convertidos para Instant
                .devolucaoEfetiva(null) // Ainda não foi devolvido
                .estadoSaida(EstadoEquipamento.COMPLETO)
                .estadoRetorno(null)
                .custoAvaria(BigDecimal.ZERO)
                .equipamento(equipamento)
                .responsavel(arnaldo) // Associando o Guia Arnaldo como responsável
                .expedicao(expedicao2) // Associando a movimentação à expedição criada
                .build();
        em.persist(movimento);


// ======================================================
//    Caverna do Diabo, localizada em Eldorado (SP)
// ======================================================

// 1. Criar Endereço
Endereco endereco2 = Endereco.builder()
    .logradouro("Avenida Governador Mario Covas")
    .numero("450")
    .bairro("Residencial Vale do Ribeira")
    .cidade("Eldorado")
    .uf("SP")
    .cep("11960000")
    .build();

// 2. Criar Pessoas (Subclasses de Pessoa)
// Responsável pela movimentação (Carlos)
GuiaEspeleologia carlos = GuiaEspeleologia.builder()
    .nome("Carlos Eduardo Rocha")
    .cpf("23456789011")
    .dataNascimento(LocalDate.of(1988, 11, 14))
    .email("carlos.guia@valedoribeira.com")
    .telefone("13997773333")
    .ativo(true)
    .endereco(endereco2)
    .numeroCredenciamento("GUIA-SP-9876")
    .nivelCertificacao("Master")
    .validadeCertificacao(LocalDate.now().plusYears(3))
    .expedicoesConcluidas(42)
    .build();
em.persist(carlos);

// Participante da Expedição (Fernanda)
Pesquisador fernanda = Pesquisador.builder()
    .nome("Fernanda Lima Mendes")
    .cpf("34567890122")
    .dataNascimento(LocalDate.of(1993, 3, 25))
    .email("fernanda.mendes@usp.br")
    .telefone("11982224444")
    .ativo(true)
    .endereco(endereco)
    .registroInstitucional("USP-2026-F")
    .areaPesquisa("Bioespeleologia e Ecossistemas")
    .titulacao("Mestrado")
    .valorDiarioBolsa(new BigDecimal("180.00"))
    .build();
em.persist(fernanda);

// 3. Criar Coordenada (Embeddable) e Caverna
Coordenada coordenadaCaverna2 = Coordenada.builder()
    .latitude(new BigDecimal("-24.638056"))
    .longitude(new BigDecimal("-48.401389"))
    .datumGeodesico("SIRGAS2000")
    .build();

Caverna caverna3 = Caverna.builder()
    .nomeOficial("Caverna do Diabo")
    .codigoAmbiental("CAV-SP-004")
    .municipio("Eldorado")
    .uf("SP")
    .altitude(new BigDecimal("185.00"))
    .extensaoConhecida(new BigDecimal("6200.00")) // Extensão aproximada total
    .dataUltimaInspecao(LocalDate.now().minusWeeks(2))
    .acessoPermitido(true)
    .coordenada(coordenadaCaverna2)
    .build();
em.persist(caverna3);

// 4. Criar Plano de Segurança
PlanoSeguranca planoSeguranca2 = PlanoSeguranca.builder()
    .procedimentosEvacuacao("Evacuação via passarelas suspensas em direção à Galeria do Órgão.")
    .pontoEncontro("Centro de Visitantes do Parque Estadual.")
    .tempoMaxSemComunicacaoMin(180)
    .telefoneEmergencia("(13) 3871-1242")
    .exigeEquipeMedica(true)
    .mapaRota(new byte[]{1, 0, 1, 0, 1, 1}) // Nova amostra de bytes
    .build();
em.persist(planoSeguranca2);

// 5. Criar a Expedição
Expedicao expedicao3 = Expedicao.builder()
    .codigo("EXP-SP-2026-10")
    .titulo("Inventário Bioespeleológico Vale do Ribeira")
    .objetivo("Coleta de amostras de microfauna e mapeamento de zonas escuras.")
    .inicioPrevisto(LocalDateTime.now().plusDays(10))
    .terminoPrevisto(LocalDateTime.now().plusDays(14))
    .orcamentoAprovado(new BigDecimal("8500.00"))
    .custoRealizado(BigDecimal.ZERO)
    .maxParticipantes(6)
    .situacao(SituacaoExpedicao.PLANEJADA)
    .cancelamentoEmergencial(false)
    .caverna(caverna3)
    .planoSeguranca(planoSeguranca2)
    .build();
em.persist(expedicao3);

// 6. Criar Participação de Pessoa na Expedição
Participacao participacao2 = Participacao.builder()
    .papel(PapelParticipante.PESQUISADOR)
    .dataConfirmacao(LocalDate.now())
    .valorDiaria(new BigDecimal("250.00"))
    .diasPrevistos(4)
    .presencaConfirmada(true)
    .observacoes("Requer lupas de campo e potes de coleta criogênica.")
    .pessoa(fernanda)
    .expedicao(expedicao3)
    .build();
em.persist(participacao2);

// 7. Criar o Equipamento
Equipamento equipamento2 = Equipamento.builder()
    .codigoPatrimonial("EQ-BIO-09")
    .nome("Lanterna Scurion 1500 Espeleo")
    .tipo(TipoEquipamento.ILUMINACAO) // Ajustado para corresponder ao novo item se houver o tipo, ou mantido conforme o ENUM do seu sistema
    .fabricante("Scurion")
    .valorAquisicao(new BigDecimal("4200.00"))
    .dataCompra(LocalDate.now().minusMonths(6))
    .ultimaManutencao(LocalDate.now().minusMonths(1))
    .situacao(SituacaoOperacional.OPERANTE)
    .exigeCalibracao(false)
    .build();
em.persist(equipamento2);

// 8. Criar a Movimentação do Equipamento associada à Expedição e ao Responsável
MovimentacaoEquipamento movimento2 = MovimentacaoEquipamento.builder()
    .retirada(Instant.now().plus(java.time.Duration.ofDays(10))) // Retirada agendada para o início da expedição
    .previsaoDevolucao(Instant.now().plus(java.time.Duration.ofDays(14))) 
    .devolucaoEfetiva(null)
    .estadoSaida(EstadoEquipamento.COMPLETO)
    .estadoRetorno(null)
    .custoAvaria(BigDecimal.ZERO)
    .equipamento(equipamento2)
    .responsavel(carlos) // Carlos como responsável pela movimentação
    .expedicao(expedicao3)
    .build();
em.persist(movimento2);

// ======================================================
//  Toca da Boa Vista, localizada em Campo Formoso (BA)
// ======================================================

// ==========================================
// 1. NOVO ENDEREÇO (Nordeste Baiano)
// ==========================================
Endereco enderecoBahia = Endereco.builder()
    .logradouro("Praça Herculano Menezes")
    .numero("s/n")
    .bairro("Centro")
    .cidade("Campo Formoso")
    .uf("BA")
    .cep("44790000")
    .build();

// ==========================================
// 2. NOVOS PARTICIPANTES
// ==========================================
// Guia e Espeleomergulhador de Resgate (Thiago)
GuiaEspeleologia thiago = GuiaEspeleologia.builder()
    .nome("Thiago Albuquerque Melo")
    .cpf("89012345677")
    .dataNascimento(LocalDate.of(1982, 6, 14))
    .email("thiago.dive@espeleo.com.br")
    .telefone("74991112222")
    .ativo(true)
    .endereco(enderecoBahia)
    .numeroCredenciamento("GUIA-BA-3321")
    .nivelCertificacao("Full Cave")
    .validadeCertificacao(LocalDate.now().plusYears(4))
    .expedicoesConcluidas(87)
    .build();
em.persist(thiago);

// Pesquisadora / Hidrogeóloga (Dra. Helena)
Pesquisador helena = Pesquisador.builder()
    .nome("Dra. Helena Vaz Antunes")
    .cpf("90123456788")
    .dataNascimento(LocalDate.of(1979, 2, 28))
    .email("helena.antunes@ufba.br")
    .telefone("71981110000")
    .ativo(true)
    .endereco(enderecoBahia)
    .registroInstitucional("UFBA-HIDRO-2026")
    .areaPesquisa("Hidrodinâmica de Aquíferos Cársticos")
    .titulacao("Pós-Doutorado")
    .valorDiarioBolsa(new BigDecimal("320.00"))
    .build();
em.persist(helena);

// ==========================================
// 3. NOVA CAVERNA E COORDENADAS
// ==========================================
Coordenada coordenadaBoaVista = Coordenada.builder()
    .latitude(new BigDecimal("-10.165833"))
    .longitude(new BigDecimal("-40.862222"))
    .datumGeodesico("SIRGAS2000")
    .build();

Caverna tocaBoaVista = Caverna.builder()
    .nomeOficial("Toca da Boa Vista")
    .codigoAmbiental("CAV-BA-001")
    .municipio("Campo Formoso")
    .uf("BA")
    .altitude(new BigDecimal("520.00"))
    .extensaoConhecida(new BigDecimal("114000.00")) // Mais de 114 km de extensão mapeada
    .dataUltimaInspecao(LocalDate.now().minusMonths(3))
    .acessoPermitido(true)
    .coordenada(coordenadaBoaVista)
    .build();
em.persist(tocaBoaVista);

// ==========================================
// 4. NOVO PLANO DE SEGURANÇA (Foco em Mergulho)
// ==========================================
PlanoSeguranca planoBoaVista = PlanoSeguranca.builder()
    .procedimentosEvacuacao("Em caso de pane de oxigênio, utilizar cilindros de segurança (bailout) previamente posicionados na Galeria Submersa II.")
    .pontoEncontro("Base Avançada Salão dos Blocos.")
    .tempoMaxSemComunicacaoMin(240) // Tempo maior devido ao mergulho síncronono
    .telefoneEmergencia("(74) 3645-1199") // SAMU Regional
    .exigeEquipeMedica(true)
    .mapaRota(new byte[]{1, 1, 0, 0, 1, 1, 0, 1})
    .build();
em.persist(planoBoaVista);

// ==========================================
// 5. A NOVA EXPEDIÇÃO
// ==========================================
Expedicao novaExpedicao = Expedicao.builder()
    .codigo("EXP-BA-2026-SUB")
    .titulo("Mapeamento de Sifões Submersos Boa Vista")
    .objetivo("Exploração e topografia subaquática do terceiro sifão interligado.")
    .inicioPrevisto(LocalDateTime.now().plusDays(30)) // Programada para daqui a um mês
    .terminoPrevisto(LocalDateTime.now().plusDays(37)) // 7 dias de duração
    .orcamentoAprovado(new BigDecimal("24500.00")) // Orçamento robusto devido aos gases (Hélio/Oxigênio)
    .custoRealizado(BigDecimal.ZERO)
    .maxParticipantes(4)
    .situacao(SituacaoExpedicao.PLANEJADA)
    .cancelamentoEmergencial(false)
    .caverna(tocaBoaVista)
    .planoSeguranca(planoBoaVista)
    .build();
em.persist(novaExpedicao);

// ==========================================
// 6. RELAÇÕES DE PARTICIPAÇÃO
// ==========================================
// Participação da Helena (Líder Científica)
Participacao partHelena = Participacao.builder()
    .papel(PapelParticipante.PESQUISADOR)
    .dataConfirmacao(LocalDate.now())
    .valorDiaria(new BigDecimal("400.00"))
    .diasPrevistos(7)
    .presencaConfirmada(true)
    .observacoes("Instalação de sensores automáticos de condutividade e nível de água.")
    .pessoa(helena)
    .expedicao(novaExpedicao)
    .build();
em.persist(partHelena);

// Participação do Thiago (Líder de Mergulho/Segurança)
Participacao partThiago = Participacao.builder()
    .papel(PapelParticipante.GUIA)
    .dataConfirmacao(LocalDate.now())
    .valorDiaria(new BigDecimal("550.00"))
    .diasPrevistos(7)
    .presencaConfirmada(true)
    .observacoes("Supervisão técnica de mergulho profundo em ambiente confinado.")
    .pessoa(thiago)
    .expedicao(novaExpedicao)
    .build();
em.persist(partThiago);

// ==========================================
// 7. EQUIPAMENTO AVANÇADO
// ==========================================
Equipamento rebreather = Equipamento.builder()
    .codigoPatrimonial("EQ-MERG-88")
    .nome("Rebreather JJ-CCR Eletrônico")
    .tipo(TipoEquipamento.CAPACETE) // Equipamento de circuito fechado para mergulho longo
    .fabricante("JJ-CCR")
    .valorAquisicao(new BigDecimal("68000.00")) // Equipamento de altíssimo custo
    .dataCompra(LocalDate.now().minusYears(1))
    .ultimaManutencao(LocalDate.now().minusWeeks(1)) // Revisado recentemente
    .situacao(SituacaoOperacional.OPERANTE)
    .exigeCalibracao(true) // Sensores de Oxigênio exigem calibração constante
    .build();
em.persist(rebreather);

// ==========================================
// 8. MOVIMENTAÇÃO DE EQUIPAMENTO
// ==========================================
MovimentacaoEquipamento movRebreather = MovimentacaoEquipamento.builder()
    .retirada(Instant.now().plus(java.time.Duration.ofDays(30))) // Alinhado ao início da expedição
    .previsaoDevolucao(Instant.now().plus(java.time.Duration.ofDays(37)))
    .devolucaoEfetiva(null)
    .estadoSaida(EstadoEquipamento.COMPLETO)
    .estadoRetorno(null)
    .custoAvaria(BigDecimal.ZERO)
    .equipamento(rebreather)
    .responsavel(thiago) // Thiago assume a responsabilidade pelo rebreather
    .expedicao(novaExpedicao)
    .build();
em.persist(movRebreather);

        

        em.getTransaction().commit();
        Long id = expedicao.getId();
        return id;
    }

    private static void titulo(String texto, Statistics stats) {
        System.out.println("\n==================== " + texto + " ====================");
        stats.clear();
    }

    private static void comandos(Statistics stats) {
        System.out.println(">>> Comandos SQL executados: " + stats.getPrepareStatementCount());
    }
}