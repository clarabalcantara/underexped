package br.com.turmalina.models.equipamento;

import java.math.BigDecimal;
import java.time.Instant;

import br.com.turmalina.models.pessoas.Pessoa;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


enum EstadoEquipamento {
    completo,
    avariado
}


@Entity 
@Table (name = "movimentacao_equipamento")
@Getter 
@Setter 
@NoArgsConstructor 
@AllArgsConstructor 
@Builder 
public class MovimentacaoEquipamento {
    @Id 
    @GeneratedValue (strategy = GenerationType.AUTO)
    public Long id;

    @Column 
    public Instant retirada;

    @Column (name = "previsao_devolucao")
    public Instant previsaoDevolucao;

    @Column (name = "devolucao_efetiva")
    public Instant devolucaoEfetiva;

    @Column (name = "estado_saida")
    public EstadoEquipamento estadoSaida;

    @Column (name = "estado_retorno")
    public EstadoEquipamento estadoRetorno;

    @Column (name = "custo_avaria")
    public BigDecimal custoAvaria;

    @ManyToOne 
    @JoinColumn (name = "equipamento", nullable = false)
    public Equipamento equipamento;

    @ManyToOne 
    @JoinColumn (name = "responsavel", nullable = false)
    public Pessoa responsavel;

    // @ManyToOne 
    // @JoinColumn (name = "expedicao", nullable = false)
    // public Expedicao expedicao;
}
