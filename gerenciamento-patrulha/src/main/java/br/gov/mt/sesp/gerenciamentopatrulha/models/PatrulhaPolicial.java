package br.gov.mt.sesp.gerenciamentopatrulha.models;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "patrulha_policial")
public class PatrulhaPolicial {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "idpatrulha",
            foreignKey = @ForeignKey(name = "patrulha_policial_idpatrulha_fkey"))
    private Patrulha patrulha;

    @Column(name = "idpolicial")
    private Long idPolicial;

    public PatrulhaPolicial() {}

    public PatrulhaPolicial(Patrulha patrulha, Long idPolicial) {
        this.patrulha = patrulha;
        this.idPolicial = idPolicial;
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

    public Long getIdPolicial() {
        return idPolicial;
    }

    public void setIdPolicial(Long idPolicial) {
        this.idPolicial = idPolicial;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PatrulhaPolicial that = (PatrulhaPolicial) o;
        return Objects.equals(id, that.id) && Objects.equals(patrulha, that.patrulha) && Objects.equals(idPolicial, that.idPolicial);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, patrulha, idPolicial);
    }
}
