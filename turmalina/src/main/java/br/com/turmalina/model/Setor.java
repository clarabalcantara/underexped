package br.com.turmalina.model;

import java.math.BigDecimal;

import br.com.turmalina.model.enums.NivelDificuldade;
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
@Table (name = "setor")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Setor {
    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    public Long id;

    @Column (nullable = false, length = 100)
    public String denominacao;

    @Enumerated (EnumType.STRING)
    @Column (name = "nivel_dificuldade", nullable = false, length = 20)
    public NivelDificuldade nivelDificuldade;

    @Column (name = "profundidade_maxima", precision = 8, scale = 2)
    public BigDecimal profundidadeMaxima;

    @Column (name = "extensao_aproximada", precision = 10, scale = 2)
    public BigDecimal extensaoAproximada;

    @Column (length = 500)
    public String descricao;

    @Column (name = "risco_inundacao", nullable = false)
    public boolean riscoInundacao;

    @Column (name = "condicao_corrente", nullable = false, length = 100)
    public String condicaoCorrente;

    @ManyToOne (fetch = FetchType.LAZY, optional = false)
    @JoinColumn (name = "caverna_id", nullable = false)
    public Caverna caverna;
}