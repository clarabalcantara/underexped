package br.com.turmalina.model;

import br.com.turmalina.model.enums.SituacaoRelatorio;
import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "relatorio_final")
public class RelatorioFinal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "expedicao_id", nullable = false, unique = true)
    private Expedicao expedicao;

    @Column(nullable = false, length = 150)
    private String titulo;

    @Column(nullable = false, columnDefinition = "text")
    private String resumo;

    @Column(name = "data_submissao", nullable = false)
    private LocalDate dataSubmissao;

    @Column(name = "total_paginas", nullable = false)
    private int totalPaginas;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private SituacaoRelatorio situacao = SituacaoRelatorio.RASCUNHO;

    @Lob
    @Column(name = "arquivo")
    private byte[] arquivo;

    @Column(name = "publicacao_autorizada", nullable = false)
    private boolean publicacaoAutorizada;

    protected RelatorioFinal() {
    }

    public RelatorioFinal(Expedicao expedicao, String titulo, String resumo,
                          LocalDate dataSubmissao, int totalPaginas) {
        this.expedicao = expedicao;
        this.titulo = titulo;
        this.resumo = resumo;
        this.dataSubmissao = dataSubmissao;
        this.totalPaginas = totalPaginas;
    }

    public Long getId() { return id; }

    public Expedicao getExpedicao() { return expedicao; }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public String getResumo() { return resumo; }
    public void setResumo(String resumo) { this.resumo = resumo; }

    public LocalDate getDataSubmissao() { return dataSubmissao; }
    public void setDataSubmissao(LocalDate dataSubmissao) { this.dataSubmissao = dataSubmissao; }

    public int getTotalPaginas() { return totalPaginas; }
    public void setTotalPaginas(int totalPaginas) { this.totalPaginas = totalPaginas; }

    public SituacaoRelatorio getSituacao() { return situacao; }
    public void setSituacao(SituacaoRelatorio situacao) { this.situacao = situacao; }

    public byte[] getArquivo() { return arquivo; }
    public void setArquivo(byte[] arquivo) { this.arquivo = arquivo; }

    public boolean isPublicacaoAutorizada() { return publicacaoAutorizada; }
    public void setPublicacaoAutorizada(boolean publicacaoAutorizada) { this.publicacaoAutorizada = publicacaoAutorizada; }
}