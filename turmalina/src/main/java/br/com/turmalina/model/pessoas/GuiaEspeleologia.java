package br.com.turmalina.model.pessoas;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Table (name = "guia_espeleologia")
@DiscriminatorValue ("GUIA")
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class GuiaEspeleologia extends Pessoa {
    // sem @Id aqui: com herança JOINED, o id é herdado de Pessoa

    @Column (name = "numero_credenciamento", nullable = false, length = 30, unique = true)
    public String numeroCredenciamento;

    @Column (name = "nivel_certificacao", nullable = false, length = 30)
    public String nivelCertificacao;

    @Column (name = "validade_certificacao", nullable = false)
    public LocalDate validadeCertificacao;

    @Column (name = "expedicoes_concluidas", nullable = false)
    public int expedicoesConcluidas;
}