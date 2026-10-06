package br.com.turmalina;

import br.com.turmalina.dto.AmostraResumo;
import br.com.turmalina.dto.ExpedicaoResumo;
import br.com.turmalina.dto.ParticipanteResumo;
import br.com.turmalina.model.*;
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