package br.com.turmalina.model.pessoas;

import java.math.BigDecimal;

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
@Table (name = "pesquisador")
@DiscriminatorValue ("PESQUISADOR")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Pesquisador extends Pessoa {
    // propositamente sem @id aqui: com herança JOINED, o id é herdado de Pessoa

    @Column (name = "registro_institucional", nullable = false, length = 30, unique = true)
    public String registroInstitucional;

    @Column (name = "area_pesquisa", nullable = false, length = 100)
    public String areaPesquisa;

    @Column (nullable = false, length = 50)
    public String titulacao;

    @Column (name = "valor_diario_bolsa", nullable = false, precision = 10, scale = 2)
    public BigDecimal valorDiarioBolsa;
}