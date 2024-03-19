package br.gov.mt.sesp.carteirafuncional.models;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "setor")
public class Setor {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Long id;

  @Column(name = "sigla")
  private String sigla;

  @Column(name = "descricao")
  private String descricao;

  @ManyToOne
  @JoinColumn(name = "idorgao",
          foreignKey = @ForeignKey(name = "setor_idorgao_fkey"))
  private Orgao orgao;

  public Long getId() {
    return this.id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getSigla() {
    return this.sigla;
  }

  public void setSigla(String sigla) {
    this.sigla = sigla;
  }

  public String getDescricao() {
    return this.descricao;
  }

  public void setDescricao(String descricao) {
    this.descricao = descricao;
  }

  public Orgao getOrgao() {
    return this.orgao;
  }

  public void setOrgao(Orgao orgao) {
    this.orgao = orgao;
  }

  @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof Setor)) {
            return false;
        }
        Setor setor = (Setor) o;
        return Objects.equals(id, setor.id) 
          && Objects.equals(sigla, setor.sigla) 
          && Objects.equals(descricao, setor.descricao) 
          && Objects.equals(orgao, setor.orgao);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, sigla, descricao, orgao);
  }

  @Override
  public String toString() {
    return ToStringBuilder.reflectionToString(this, ToStringStyle.JSON_STYLE);
  }
}
