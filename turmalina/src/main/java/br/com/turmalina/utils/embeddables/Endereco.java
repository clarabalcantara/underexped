package br.com.turmalina.utils.embeddables;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Embeddable 
@Getter 
@Setter 
@Builder 
public class Endereco {
    @Column 
    public String logradouro;

    @Column 
    public String numero;

    @Column 
    public String complemento;

    @Column 
    public String bairro;

    @Column 
    public String cidade;

    @Column (length = 2)
    public String uf;

    @Column (length = 8)
    public String cep;
}
