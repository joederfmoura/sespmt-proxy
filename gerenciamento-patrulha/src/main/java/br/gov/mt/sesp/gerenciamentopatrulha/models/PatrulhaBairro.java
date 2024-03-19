package br.gov.mt.sesp.gerenciamentopatrulha.models;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "bairro_patrulha")
public class PatrulhaBairro {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "idpatrulha",
            foreignKey = @ForeignKey(name = "bairro_patrulha_idpatrulha_fkey"))
    private Patrulha patrulha;

    @Column(name = "idbairro")
    private Long idBairro;

    public PatrulhaBairro() {}

    public PatrulhaBairro(Patrulha patrulha, Long idBairro) {
        this.patrulha = patrulha;
        this.idBairro = idBairro;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Patrulha getPatrulha() {
        return patrulha;
    }

    public void setPatrulha(Patrulha patrulha) {
        this.patrulha = patrulha;
    }

    public Long getIdBairro() {
        return idBairro;
    }

    public void setIdBairro(Long idBairro) {
        this.idBairro = idBairro;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PatrulhaBairro that = (PatrulhaBairro) o;
        return Objects.equals(id, that.id) && Objects.equals(patrulha, that.patrulha) && Objects.equals(idBairro, that.idBairro);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, patrulha, idBairro);
    }
}
