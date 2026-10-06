package br.com.turmalina.utils.embeddables;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Embeddable
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Endereco {
    @Column (length = 120)
    public String logradouro;

    @Column (length = 10)
    public String numero;

    @Column (length = 60)
    public String complemento;

    @Column (length = 80)
    public String bairro;

    @Column (length = 100)
    public String cidade;

    @Column (length = 2)
    public String uf;

    @Column (length = 8)
    public String cep;
}