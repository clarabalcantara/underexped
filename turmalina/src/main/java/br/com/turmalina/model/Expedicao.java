package br.com.turmalina.model;

import br.com.turmalina.model.enums.SituacaoExpedicao;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "expedicao")
public class Expedicao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 20, unique = true)
    private String codigo;

    @Column(nullable = false, length = 150)
    private String titulo;

    @Column(nullable = false, columnDefinition = "text")
    private String objetivo;


    @Column(name = "inicio_previsto", nullable = false)
    private LocalDateTime inicioPrevisto;

    @Column(name = "termino_previsto", nullable = false)
    private LocalDateTime terminoPrevisto;

    @Column(name = "orcamento_aprovado", nullable = false, precision = 12, scale = 2)
    private BigDecimal orcamentoAprovado;

    @Column(name = "custo_realizado", precision = 12, scale = 2)
    private BigDecimal custoRealizado;

    @Column(name = "max_participantes", nullable = false)
    private int maxParticipantes;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private SituacaoExpedicao situacao = SituacaoExpedicao.PLANEJADA;

    @Column(name = "cancelamento_emergencial", nullable = false)
    private boolean cancelamentoEmergencial;

    // Sem cascata: a caverna tem ciclo de vida próprio.
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "caverna_id", nullable = false)
    private Caverna caverna;

    @ManyToMany
    @JoinTable(name = "expedicao_setor",
            joinColumns = @JoinColumn(name = "expedicao_id"),
            inverseJoinColumns = @JoinColumn(name = "setor_id"))
    private Set<Setor> setores = new HashSet<>();


    @OneToOne(fetch = FetchType.LAZY, optional = false,
            cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "plano_seguranca_id", nullable = false, unique = true)
    private PlanoSeguranca planoSeguranca;

    @OneToMany(mappedBy = "expedicao", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Participacao> participacoes = new ArrayList<>();

    @OneToMany(mappedBy = "expedicao", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Coleta> coletas = new ArrayList<>();

    // AutorizacaoAmbiental e RelatorioFinal NÃO são mapeados aqui de propósito:
    // assim nenhuma consulta de expedição os carrega (nem seus binários).

    protected Expedicao() {

    }

    public Expedicao(String codigo, String titulo, String objetivo,
                     LocalDateTime inicioPrevisto, LocalDateTime terminoPrevisto,
                     BigDecimal orcamentoAprovado, int maxParticipantes,
                     Caverna caverna, PlanoSeguranca planoSeguranca) {
        this.codigo = codigo;
        this.titulo = titulo;
        this.objetivo = objetivo;
        this.inicioPrevisto = inicioPrevisto;
        this.terminoPrevisto = terminoPrevisto;
        this.orcamentoAprovado = orcamentoAprovado;
        this.maxParticipantes = maxParticipantes;
        this.caverna = caverna;
        this.planoSeguranca = planoSeguranca;
        planoSeguranca.setExpedicao(this); // mantém o lado inverso coerente
    }

    // ---------- Métodos auxiliares:
    public void setPlanoSeguranca(PlanoSeguranca plano) {
        this.planoSeguranca = plano;
        if (plano != null) {
            plano.setExpedicao(this);
        }
    }

    public void adicionarSetor(Setor setor) {
        setores.add(setor);
    }

    public void adicionarParticipacao(Participacao participacao) {
        participacoes.add(participacao);
        participacao.setExpedicao(this);
    }

    public void removerParticipacao(Participacao participacao) {
        participacoes.remove(participacao); // orphanRemoval apaga a linha no commit
        participacao.setExpedicao(null);
    }

    public void adicionarColeta(Coleta coleta) {
        coletas.add(coleta);
        coleta.setExpedicao(this);
    }

    public void removerColeta(Coleta coleta) {
        coletas.remove(coleta);
        coleta.setExpedicao(null);
    }

    // ---------- Regras que o banco não consegue garantir sozinho

    @PrePersist
    @PreUpdate
    private void validar() {
        if (!terminoPrevisto.isAfter(inicioPrevisto)) {
            throw new IllegalStateException("O término previsto deve ser depois do início previsto.");
        }
        if (participacoes.size() > maxParticipantes) {
            throw new IllegalStateException("A expedição " + codigo + " excede o máximo de "
                    + maxParticipantes + " participantes.");
        }
        for (Setor setor : setores) {
            if (!Objects.equals(setor.getCaverna().getId(), caverna.getId())) {
                throw new IllegalStateException("O setor " + setor.getDenominacao()
                        + " não pertence à caverna da expedição.");
            }
        }
    }

    // ---------- Getters e setters

    public Long getId() { return id; }

    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public String getObjetivo() { return objetivo; }
    public void setObjetivo(String objetivo) { this.objetivo = objetivo; }

    public LocalDateTime getInicioPrevisto() { return inicioPrevisto; }
    public void setInicioPrevisto(LocalDateTime inicioPrevisto) { this.inicioPrevisto = inicioPrevisto; }

    public LocalDateTime getTerminoPrevisto() { return terminoPrevisto; }
    public void setTerminoPrevisto(LocalDateTime terminoPrevisto) { this.terminoPrevisto = terminoPrevisto; }

    public BigDecimal getOrcamentoAprovado() { return orcamentoAprovado; }
    public void setOrcamentoAprovado(BigDecimal orcamentoAprovado) { this.orcamentoAprovado = orcamentoAprovado; }

    public BigDecimal getCustoRealizado() { return custoRealizado; }
    public void setCustoRealizado(BigDecimal custoRealizado) { this.custoRealizado = custoRealizado; }

    public int getMaxParticipantes() { return maxParticipantes; }
    public void setMaxParticipantes(int maxParticipantes) { this.maxParticipantes = maxParticipantes; }

    public SituacaoExpedicao getSituacao() { return situacao; }
    public void setSituacao(SituacaoExpedicao situacao) { this.situacao = situacao; }

    public boolean isCancelamentoEmergencial() { return cancelamentoEmergencial; }
    public void setCancelamentoEmergencial(boolean cancelamentoEmergencial) { this.cancelamentoEmergencial = cancelamentoEmergencial; }

    public Caverna getCaverna() { return caverna; }
    public void setCaverna(Caverna caverna) { this.caverna = caverna; }

    public Set<Setor> getSetores() { return setores; }

    public PlanoSeguranca getPlanoSeguranca() { return planoSeguranca; }

    public List<Participacao> getParticipacoes() { return participacoes; }

    public List<Coleta> getColetas() { return coletas; }
}
