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
    @GeneratedValue (strategy = GenerationType.AUTO)
    public Long id;

    @Column (name = "nome_oficial", length = 50)
    public String nomeOficial;

    @Column (name = "codigo_ambiental")
    public String codigoAmbiental;

    @Column    
    public String municipio;

    @Column 
    public String uf;

    @Column 
    public BigDecimal altitude;

    @Column (name = "extensao_conhecida")
    public BigDecimal extensaoConhecida;

    @Column (name = "data_ultima_inspecao")
    public LocalDate dataUltimaInspecao;

    @Embedded 
    public Coordenada Coordenada;

    @OneToMany (mappedBy = "caverna", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    public List<Setor> setores = new ArrayList<>();

    public void addSetor(Setor setor) {
        this.setores.add(setor);
    }
}
