package br.com.turmalina.utils.embeddables;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class Coordenada {
    @Column (precision = 9, scale = 6)
    public BigDecimal latitude;

    @Column (precision = 9, scale = 6)
    public BigDecimal longitude;

    @Column (name = "datum_geodesico", length = 20)
    public String datumGeodesico;
}
