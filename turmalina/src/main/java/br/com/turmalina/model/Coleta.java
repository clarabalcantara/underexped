package br.com.turmalina.model;

import br.com.turmalina.model.enums.SituacaoValidacao;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "coleta")
public class Coleta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "expedicao_id", nullable = false)
    private Expedicao expedicao;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "setor_id", nullable = false)
    private Setor setor;


    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "pesquisador_id", nullable = false)
    private Pesquisador responsavel;

    @Column(name = "data_hora", nullable = false)
    private Instant dataHora;

    @Column(nullable = false, length = 100)
    private String metodo;

    @Column(name = "descricao_ponto", nullable = false, length = 500)
    private String descricaoPonto;

    @Column(precision = 5, scale = 2)
    private BigDecimal temperatura;

    @Column(name = "umidade_relativa", precision = 5, scale = 2)
    private BigDecimal umidadeRelativa;

    @Column(precision = 8, scale = 2)
    private BigDecimal profundidade;

    @Column(columnDefinition = "text")
    private String observacoes;

    @Enumerated(EnumType.STRING)
    @Column(name = "situacao_validacao", nullable = false, length = 20)
    private SituacaoValidacao situacaoValidacao = SituacaoValidacao.PENDENTE;

    @OneToMany(mappedBy = "coleta", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Amostra> amostras = new ArrayList<>();

    protected Coleta() {
    }

    public Coleta(Setor setor, Pesquisador responsavel, Instant dataHora,
                  String metodo, String descricaoPonto) {
        this.setor = setor;
        this.responsavel = responsavel;
        this.dataHora = dataHora;
        this.metodo = metodo;
        this.descricaoPonto = descricaoPonto;
    }

    public void adicionarAmostra(Amostra amostra) {
        amostras.add(amostra);
        amostra.setColeta(this);
    }

    public void removerAmostra(Amostra amostra) {
        amostras.remove(amostra);
        amostra.setColeta(null);
    }

    @PrePersist
    @PreUpdate
    private void validar() {
        if (!Objects.equals(setor.getCaverna().getId(), expedicao.getCaverna().getId())) {
            throw new IllegalStateException("O setor da coleta não pertence à caverna da expedição.");
        }
    }

    public Long getId() { return id; }

    public Expedicao getExpedicao() { return expedicao; }
    void setExpedicao(Expedicao expedicao) { this.expedicao = expedicao; }

    public Setor getSetor() { return setor; }
    public void setSetor(Setor setor) { this.setor = setor; }

    public Pesquisador getResponsavel() { return responsavel; }
    public void setResponsavel(Pesquisador responsavel) { this.responsavel = responsavel; }

    public Instant getDataHora() { return dataHora; }
    public void setDataHora(Instant dataHora) { this.dataHora = dataHora; }

    public String getMetodo() { return metodo; }
    public void setMetodo(String metodo) { this.metodo = metodo; }

    public String getDescricaoPonto() { return descricaoPonto; }
    public void setDescricaoPonto(String descricaoPonto) { this.descricaoPonto = descricaoPonto; }

    public BigDecimal getTemperatura() { return temperatura; }
    public void setTemperatura(BigDecimal temperatura) { this.temperatura = temperatura; }

    public BigDecimal getUmidadeRelativa() { return umidadeRelativa; }
    public void setUmidadeRelativa(BigDecimal umidadeRelativa) { this.umidadeRelativa = umidadeRelativa; }

    public BigDecimal getProfundidade() { return profundidade; }
    public void setProfundidade(BigDecimal profundidade) { this.profundidade = profundidade; }

    public String getObservacoes() { return observacoes; }
    public void setObservacoes(String observacoes) { this.observacoes = observacoes; }

    public SituacaoValidacao getSituacaoValidacao() { return situacaoValidacao; }
    public void setSituacaoValidacao(SituacaoValidacao situacaoValidacao) { this.situacaoValidacao = situacaoValidacao; }

    public List<Amostra> getAmostras() { return amostras; }
}