package br.com.turmalina.models.equipamento;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


enum TipoEquipamento {
    capacete,
    iluminacao,
    calcado,
    vestimenta,
    luva,
    mochila,
    cadeirinha,
    ascensor,
    descensor,
    mosquetao,
    mailon,
    corda
}

enum SituacaoOperacional {
    operante,
    indisponivel
}


@Entity 
@Table (name = "equipamento")
@Getter 
@Setter 
@NoArgsConstructor 
@AllArgsConstructor 
@Builder 
public class Equipamento {
    @Id 
    @GeneratedValue (strategy = GenerationType.AUTO)
    public Long id;

    @Column (name = "codigo_patrimonial")
    public String codigoPatrimonial;

    @Column (name = "tipo_equipamento")
    public TipoEquipamento tipo;

    @Column 
    public String fabricante;

    @Column (name = "valor_aquisicao")
    public BigDecimal valorAquisicao;

    @Column (name = "data_compra")
    public LocalDate dataCompra;

    @Column (name = "ultima_manutencao")
    public LocalDate ultimaManutencao;

    @Column 
    public SituacaoOperacional situacao;

    @Column (name = "exige_calibracao")
    public Boolean exigeCalibracao;

    @OneToMany (mappedBy = "equipamento", fetch = FetchType.LAZY)
    @Builder.Default
    public List<MovimentacaoEquipamento> movimentacoes = new ArrayList<>();

    public void addMovimentacoes(MovimentacaoEquipamento movimento) {
        this.movimentacoes.add(movimento);
    }
}
