package br.com.turmalina.model;

import java.math.BigDecimal;
import java.time.Instant;

import br.com.turmalina.model.Expedicao;
import br.com.turmalina.model.enums.EstadoEquipamento;
import br.com.turmalina.model.pessoas.Pessoa;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
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

@Entity
@Table (name = "movimentacao_equipamento")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MovimentacaoEquipamento {
    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    public Long id;

    @Column (nullable = false)
    public Instant retirada;

    @Column (name = "previsao_devolucao", nullable = false)
    public Instant previsaoDevolucao;

    @Column (name = "devolucao_efetiva")
    public Instant devolucaoEfetiva;

    @Enumerated (EnumType.STRING)
    @Column (name = "estado_saida", nullable = false, length = 20)
    public EstadoEquipamento estadoSaida;

    @Enumerated (EnumType.STRING)
    @Column (name = "estado_retorno", length = 20)
    public EstadoEquipamento estadoRetorno;

    @Column (name = "custo_avaria", precision = 12, scale = 2)
    public BigDecimal custoAvaria;

    @ManyToOne (fetch = FetchType.LAZY, optional = false)
    @JoinColumn (name = "equipamento_id", nullable = false)
    public Equipamento equipamento;

    @ManyToOne (fetch = FetchType.LAZY, optional = false)
    @JoinColumn (name = "responsavel_id", nullable = false)
    public Pessoa responsavel;

    @ManyToOne (fetch = FetchType.LAZY, optional = false)
    @JoinColumn (name = "expedicao_id", nullable = false)
    public Expedicao expedicao;
}