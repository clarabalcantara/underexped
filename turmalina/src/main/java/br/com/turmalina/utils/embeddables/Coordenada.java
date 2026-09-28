package br.com.turmalina.utils.embeddables;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter 
@Setter 
@Builder 
@Embeddable 
public class Coordenada {
    @Column 
    public BigDecimal latitude;

    @Column 
    public BigDecimal longitude;

    @Column (name = "datum_geodesio")
    public String datumGeodesio;

    public Coordenada (BigDecimal latitude, BigDecimal longitude, String datumGeodesio) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.datumGeodesio = datumGeodesio;
    }
}
