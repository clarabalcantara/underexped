package br.com.turmalina.models;

import java.math.BigDecimal;

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


enum NivelDificuldade {
    baixo,
    moderado,
    alto,
    extremo
}


@Entity 
@Table (name = "setor")
@Getter 
@Setter 
@AllArgsConstructor 
@NoArgsConstructor 
@Builder 
public class Setor {
    @Id 
    @GeneratedValue (strategy = GenerationType.AUTO)
    public Long id;

    @Column 
    public String denominacao;

    @Column (name = "nivel_dificuldade")
    public NivelDificuldade nivelDificuldade;

    @Column (name = "profundidade_maxima")
    public BigDecimal profundidadeMaxima;

    @Column (name = "extensao_aproximada")
    public BigDecimal extensaoAproximada;

    public String descricao;

    @Column (name = "risco_inundacao")
    public Boolean riscoInundacao;

    @Column (name = "condicao_corrente")
    public String condicaoCorrente;

    @ManyToOne 
    @JoinColumn (name = "caverna", nullable = false)
    public Caverna caverna;
}
