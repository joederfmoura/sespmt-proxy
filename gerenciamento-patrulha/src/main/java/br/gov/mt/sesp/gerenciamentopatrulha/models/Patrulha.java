package br.gov.mt.sesp.gerenciamentopatrulha.models;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import javax.persistence.*;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "patrulha")
public class Patrulha {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Long id;

  @Column(name = "viatura")
  private String viatura;

  @OneToMany(mappedBy = "patrulha",
          fetch = FetchType.EAGER,
          cascade = CascadeType.ALL)
  private Set<PatrulhaBairro> bairros;

  @OneToMany(mappedBy = "patrulha",
          fetch = FetchType.EAGER,
          cascade = CascadeType.ALL)
  private Set<PatrulhaPolicial> policiais;

  public Long getId() {
    return this.id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getViatura() {
    return this.viatura;
  }

  public void setViatura(String viatura) {
    this.viatura = viatura;
  }

  public Set<PatrulhaBairro> getBairros() {
    if (bairros == null) {
      return Collections.emptySet();
    }

    return Collections.unmodifiableSet(bairros);
  }

  public void incluirBairro(Long idBairro) {
    if (bairros == null) {
      bairros = new HashSet<>();
    }

    bairros.add(new PatrulhaBairro(this, idBairro));
  }

  public Set<PatrulhaPolicial> getPoliciais() {
    if (policiais == null) {
      return Collections.emptySet();
    }

    return Collections.unmodifiableSet(policiais);
  }

  public void incluirPolicial(Long idPolicial) {
    if (policiais == null) {
      policiais = new HashSet<>();
    }

    policiais.add(new PatrulhaPolicial(this, idPolicial));
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Patrulha patrulha = (Patrulha) o;
    return Objects.equals(id, patrulha.id) && Objects.equals(viatura, patrulha.viatura);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, viatura);
  }

  @Override
  public String toString() {
    return ToStringBuilder.reflectionToString(this, ToStringStyle.JSON_STYLE);
  }
}
