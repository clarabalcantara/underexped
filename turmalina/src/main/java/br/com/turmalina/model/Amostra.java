package br.com.turmalina.model;

import br.com.turmalina.model.enums.CategoriaAmostra;
import br.com.turmalina.model.enums.CondicaoConservacao;
import br.com.turmalina.model.enums.UnidadeMedida;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "amostra")
public class Amostra {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "coleta_id", nullable = false)
    private Coleta coleta;

    @Column(name = "codigo_campo", nullable = false, length = 30, unique = true)
    private String codigoCampo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private CategoriaAmostra categoria;

    @Column(nullable = false, precision = 12, scale = 4)
    private BigDecimal quantidade;

    @Enumerated(EnumType.STRING)
    @Column(name = "unidade_medida", nullable = false, length = 10)
    private UnidadeMedida unidadeMedida;

    @Column(name = "data_acondicionamento", nullable = false)
    private LocalDate dataAcondicionamento;

    @Enumerated(EnumType.STRING)
    @Column(name = "condicao_conservacao", nullable = false, length = 30)
    private CondicaoConservacao condicaoConservacao;

    @Column(name = "material_perigoso", nullable = false)
    private boolean materialPerigoso;

    // Binário como objeto grande; excluído das listagens por projeção (AmostraResumo)
    @Lob
    @Column(name = "fotografia")
    private byte[] fotografia;

    @Column(columnDefinition = "text")
    private String observacoes;

    protected Amostra() {
    }

    public Amostra(String codigoCampo, CategoriaAmostra categoria, BigDecimal quantidade,
                   UnidadeMedida unidadeMedida, LocalDate dataAcondicionamento,
                   CondicaoConservacao condicaoConservacao, boolean materialPerigoso) {
        this.codigoCampo = codigoCampo;
        this.categoria = categoria;
        this.quantidade = quantidade;
        this.unidadeMedida = unidadeMedida;
        this.dataAcondicionamento = dataAcondicionamento;
        this.condicaoConservacao = condicaoConservacao;
        this.materialPerigoso = materialPerigoso;
    }

    public Long getId() { return id; }

    public Coleta getColeta() { return coleta; }
    void setColeta(Coleta coleta) { this.coleta = coleta; }

    public String getCodigoCampo() { return codigoCampo; }
    public void setCodigoCampo(String codigoCampo) { this.codigoCampo = codigoCampo; }

    public CategoriaAmostra getCategoria() { return categoria; }
    public void setCategoria(CategoriaAmostra categoria) { this.categoria = categoria; }

    public BigDecimal getQuantidade() { return quantidade; }
    public void setQuantidade(BigDecimal quantidade) { this.quantidade = quantidade; }

    public UnidadeMedida getUnidadeMedida() { return unidadeMedida; }
    public void setUnidadeMedida(UnidadeMedida unidadeMedida) { this.unidadeMedida = unidadeMedida; }

    public LocalDate getDataAcondicionamento() { return dataAcondicionamento; }
    public void setDataAcondicionamento(LocalDate dataAcondicionamento) { this.dataAcondicionamento = dataAcondicionamento; }

    public CondicaoConservacao getCondicaoConservacao() { return condicaoConservacao; }
    public void setCondicaoConservacao(CondicaoConservacao condicaoConservacao) { this.condicaoConservacao = condicaoConservacao; }

    public boolean isMaterialPerigoso() { return materialPerigoso; }
    public void setMaterialPerigoso(boolean materialPerigoso) { this.materialPerigoso = materialPerigoso; }

    public byte[] getFotografia() { return fotografia; }
    public void setFotografia(byte[] fotografia) { this.fotografia = fotografia; }

    public String getObservacoes() { return observacoes; }
    public void setObservacoes(String observacoes) { this.observacoes = observacoes; }
}