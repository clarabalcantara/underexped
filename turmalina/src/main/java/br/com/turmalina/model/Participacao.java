package br.com.turmalina.model;

import br.com.turmalina.model.enums.PapelParticipante;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;

/** LEMBRETE !!!!!!!!!!!!!
 * Entidade associativa entre Expedicao e Pessoa: a ligação tem atributos próprios,
 * por isso não pode ser um simples @ManyToMany.
 * O UNIQUE (expedicao_id, pessoa_id) impede a mesma pessoa duas vezes na mesma expedição.
 */
@Entity
@Table(name = "participacao",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_participacao_expedicao_pessoa",
                columnNames = {"expedicao_id", "pessoa_id"}))
public class Participacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "expedicao_id", nullable = false)
    private Expedicao expedicao;


    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "pessoa_id", nullable = false)
    private Pessoa pessoa;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private PapelParticipante papel;

    @Column(name = "data_confirmacao")
    private LocalDate dataConfirmacao;

    @Column(name = "valor_diaria", nullable = false, precision = 10, scale = 2)
    private BigDecimal valorDiaria;

    @Column(name = "dias_previstos", nullable = false)
    private int diasPrevistos;

    @Column(name = "presenca_confirmada", nullable = false)
    private boolean presencaConfirmada;

    @Column(length = 500)
    private String observacoes;

    protected Participacao() {
    }

    public Participacao(Pessoa pessoa, PapelParticipante papel,
                        BigDecimal valorDiaria, int diasPrevistos) {
        this.pessoa = pessoa;
        this.papel = papel;
        this.valorDiaria = valorDiaria;
        this.diasPrevistos = diasPrevistos;
    }

    public Long getId() { return id; }

    public Expedicao getExpedicao() { return expedicao; }
    void setExpedicao(Expedicao expedicao) { this.expedicao = expedicao; }

    public Pessoa getPessoa() { return pessoa; }

    public PapelParticipante getPapel() { return papel; }
    public void setPapel(PapelParticipante papel) { this.papel = papel; }

    public LocalDate getDataConfirmacao() { return dataConfirmacao; }
    public void setDataConfirmacao(LocalDate dataConfirmacao) { this.dataConfirmacao = dataConfirmacao; }

    public BigDecimal getValorDiaria() { return valorDiaria; }
    public void setValorDiaria(BigDecimal valorDiaria) { this.valorDiaria = valorDiaria; }

    public int getDiasPrevistos() { return diasPrevistos; }
    public void setDiasPrevistos(int diasPrevistos) { this.diasPrevistos = diasPrevistos; }

    public boolean isPresencaConfirmada() { return presencaConfirmada; }
    public void setPresencaConfirmada(boolean presencaConfirmada) { this.presencaConfirmada = presencaConfirmada; }

    public String getObservacoes() { return observacoes; }
    public void setObservacoes(String observacoes) { this.observacoes = observacoes; }
}