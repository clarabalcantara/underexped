package br.com.turmalina.model.pessoas;

import java.time.LocalDate;

import br.com.turmalina.utils.embeddables.Endereco;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity 
@Table (name = "pessoa")
@Inheritance (strategy = InheritanceType.JOINED)
@Getter
@Setter 
@AllArgsConstructor 
@NoArgsConstructor 
@SuperBuilder 
public class Pessoa {
    @Id 
    @GeneratedValue (strategy = GenerationType.AUTO)
    public Long id;

    @Column 
    public String nome;

    @Column 
    public String cpf;

    @Column (name = "data_nascimento")
    public LocalDate dataNascimento;

    @Column 
    public String email;

    @Column 
    public String telefone;

    @Column 
    public Boolean ativo;

    @Embedded 
    public Endereco endereco;
}
