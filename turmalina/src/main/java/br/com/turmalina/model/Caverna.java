package br.com.turmalina.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import br.com.turmalina.utils.embeddables.Coordenada;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
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

@Entity
@Table (name = "caverna")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Caverna {
    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    public Long id;

    @Column (name = "nome_oficial", nullable = false, length = 150)
    public String nomeOficial;

    @Column (name = "codigo_ambiental", nullable = false, length = 30, unique = true)
    public String codigoAmbiental;

    @Column (nullable = false, length = 100)
    public String municipio;

    @Column (nullable = false, length = 2)
    public String uf;

    @Column (precision = 7, scale = 2)
    public BigDecimal altitude;

    @Column (name = "extensao_conhecida", precision = 10, scale = 2)
    public BigDecimal extensaoConhecida;

    @Column (name = "data_ultima_inspecao")
    public LocalDate dataUltimaInspecao;

    @Column (name = "acesso_permitido", nullable = false)
    public boolean acessoPermitido;

    @Embedded
    public Coordenada coordenada;

    @OneToMany (mappedBy = "caverna", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    public List<Setor> setores = new ArrayList<>();

    public void addSetor(Setor setor) {
        this.setores.add(setor);
        setor.setCaverna(this);
    }
}