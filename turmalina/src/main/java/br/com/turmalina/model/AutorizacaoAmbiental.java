package br.com.turmalina.model;

import br.com.turmalina.model.enums.SituacaoAutorizacao;
import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "autorizacao_ambiental")
public class AutorizacaoAmbiental {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "expedicao_id", nullable = false, unique = true)
    private Expedicao expedicao;

    @Column(nullable = false, length = 40, unique = true)
    private String numero;

    @Column(name = "orgao_emissor", nullable = false, length = 100)
    private String orgaoEmissor;

    @Column(name = "data_emissao", nullable = false)
    private LocalDate dataEmissao;

    @Column(name = "data_validade", nullable = false)
    private LocalDate dataValidade;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private SituacaoAutorizacao situacao = SituacaoAutorizacao.SOLICITADA;

    @Column(columnDefinition = "text")
    private String observacoes;

    @Lob
    @Column(name = "pdf_assinado")
    private byte[] pdfAssinado;

    protected AutorizacaoAmbiental() {
    }

    public AutorizacaoAmbiental(Expedicao expedicao, String numero, String orgaoEmissor,
                                LocalDate dataEmissao, LocalDate dataValidade) {
        this.expedicao = expedicao;
        this.numero = numero;
        this.orgaoEmissor = orgaoEmissor;
        this.dataEmissao = dataEmissao;
        this.dataValidade = dataValidade;
    }

    @PrePersist
    @PreUpdate
    private void validar() {
        if (dataValidade.isBefore(dataEmissao)) {
            throw new IllegalStateException("A validade não pode ser anterior à emissão.");
        }
    }

    public Long getId() { return id; }

    public Expedicao getExpedicao() { return expedicao; }

    public String getNumero() { return numero; }
    public void setNumero(String numero) { this.numero = numero; }

    public String getOrgaoEmissor() { return orgaoEmissor; }
    public void setOrgaoEmissor(String orgaoEmissor) { this.orgaoEmissor = orgaoEmissor; }

    public LocalDate getDataEmissao() { return dataEmissao; }
    public void setDataEmissao(LocalDate dataEmissao) { this.dataEmissao = dataEmissao; }

    public LocalDate getDataValidade() { return dataValidade; }
    public void setDataValidade(LocalDate dataValidade) { this.dataValidade = dataValidade; }

    public SituacaoAutorizacao getSituacao() { return situacao; }
    public void setSituacao(SituacaoAutorizacao situacao) { this.situacao = situacao; }

    public String getObservacoes() { return observacoes; }
    public void setObservacoes(String observacoes) { this.observacoes = observacoes; }

    public byte[] getPdfAssinado() { return pdfAssinado; }
    public void setPdfAssinado(byte[] pdfAssinado) { this.pdfAssinado = pdfAssinado; }
}