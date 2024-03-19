package br.gov.mt.sesp.carteirafuncional.models;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "orgao")
public class Orgao {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Long id;

  @Column(name = "sigla")
  private String sigla;

  @Column(name = "descricao")
  private String descricao;

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

  @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof Orgao)) {
            return false;
        }
        Orgao orgao = (Orgao) o;
        return Objects.equals(id, orgao.id) 
          && Objects.equals(sigla, orgao.sigla) 
          && Objects.equals(descricao, orgao.descricao);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, sigla, descricao);
  }

  @Override
  public String toString() {
    return ToStringBuilder.reflectionToString(this, ToStringStyle.JSON_STYLE);
  }
}
