package br.gov.mt.sesp.gerenciamentopatrulha.dtos;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.List;

public class PatrulhaDTO {

  private Long id;

  private String viatura;

  private List<PatrulhaBairroDTO> bairros;

  private List<PatrulhaPolicialDTO> policiais;

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

  public List<PatrulhaBairroDTO> getBairros() {
    return bairros;
  }

  public void setBairros(List<PatrulhaBairroDTO> bairros) {
    this.bairros = bairros;
  }

  public List<PatrulhaPolicialDTO> getPoliciais() {
    return policiais;
  }

  public void setPoliciais(List<PatrulhaPolicialDTO> policiais) {
    this.policiais = policiais;
  }

  @Override
  public String toString() {
    return ToStringBuilder.reflectionToString(this, ToStringStyle.JSON_STYLE);

  }
}
