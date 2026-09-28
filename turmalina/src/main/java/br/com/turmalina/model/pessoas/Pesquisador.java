package br.com.turmalina.model.pessoas;

import java.math.BigDecimal;

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
@Table (name = "pesquisador")
@Getter 
@Setter 
@NoArgsConstructor 
@AllArgsConstructor 
@SuperBuilder 
public class Pesquisador extends Pessoa {
    @Id 
    @GeneratedValue (strategy = GenerationType.AUTO)
    public Long id;

    @Column (name = "registro_institucional")
    public String registroInstitucional;

    @Column (name = "area_pesquisa")
    public String areaPesquisa;

    @Column 
    public String titulacao;

    @Column (name = "valor_diario_bolsa")
    public BigDecimal valorDiarioBolsa;
}
