package br.com.turmalina.models.pessoas;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity 
@Table (name = "guia_espeleologia")
@Setter 
@Getter
@AllArgsConstructor 
@NoArgsConstructor 
@SuperBuilder 
public class GuiaEspeleologia extends Pessoa {
    @Id 
    @GeneratedValue (strategy = GenerationType.AUTO)
    public Long id;

    @Column (name = "numero_credenciamento")
    public String numeroCredenciamento;

    @Column (name = "nivel_certificacao")
    public String nivelCertificacao;

    @Column (name = "validade_certificacao")
    public LocalDate validadeCertificacao;

    @Column (name = "expedicoes_concluidas")
    public int expedicoesConcluidas;
}
