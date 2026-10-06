package br.com.turmalina.model;

import java.math.BigDecimal;
import java.time.LocalDate;

import br.com.turmalina.model.enums.SituacaoOperacional;
import br.com.turmalina.model.enums.TipoEquipamento;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table (name = "equipamento")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Equipamento {
    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    public Long id;

    @Column (name = "codigo_patrimonial", nullable = false, length = 30, unique = true)
    public String codigoPatrimonial;

    @Column (nullable = false, length = 100)
    public String nome;

    @Enumerated (EnumType.STRING)
    @Column (name = "tipo_equipamento", nullable = false, length = 30)
    public TipoEquipamento tipo;

    @Column (length = 100)
    public String fabricante;

    @Column (name = "valor_aquisicao", nullable = false, precision = 12, scale = 2)
    public BigDecimal valorAquisicao;

    @Column (name = "data_compra", nullable = false)
    public LocalDate dataCompra;

    @Column (name = "ultima_manutencao")
    public LocalDate ultimaManutencao;

    @Enumerated (EnumType.STRING)
    @Column (nullable = false, length = 20)
    public SituacaoOperacional situacao;

    @Column (name = "exige_calibracao", nullable = false)
    public boolean exigeCalibracao;

    // sem lista de movimentações aqui de propósito --> consultar um equipamento
    // nunca carrega o histórico completo (exigência do enunciado)
}