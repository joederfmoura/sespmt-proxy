package br.gov.mt.sesp.registrogeral.dtos;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.io.Serializable;

public class BairroDTO implements Serializable {

  private Long id;

  private String nome;

  private CidadeDTO cidade;

  public Long getId() {
    return this.id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getNome() {
    return this.nome;
  }

  public void setNome(String nome) {
    this.nome = nome;
  }

  public CidadeDTO getCidade() {
    return this.cidade;
  }

  public void setCidade(CidadeDTO cidade) {
    this.cidade = cidade;
  }

  @Override
  public String toString() {
    return ToStringBuilder.reflectionToString(this, ToStringStyle.JSON_STYLE);
  }
}
