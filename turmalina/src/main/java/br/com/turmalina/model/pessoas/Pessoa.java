package br.com.turmalina.model.pessoas;

import java.time.LocalDate;

import br.com.turmalina.utils.embeddables.Endereco;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorColumn;
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
@DiscriminatorColumn (name = "tipo_pessoa")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public abstract class Pessoa {
    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    public Long id;

    @Column (nullable = false, length = 150)
    public String nome;

    @Column (nullable = false, length = 11, unique = true)
    public String cpf;

    @Column (name = "data_nascimento", nullable = false)
    public LocalDate dataNascimento;

    @Column (nullable = false, length = 120)
    public String email;

    @Column (length = 20)
    public String telefone;

    @Column (nullable = false)
    public boolean ativo;

    @Embedded
    public Endereco endereco;
}