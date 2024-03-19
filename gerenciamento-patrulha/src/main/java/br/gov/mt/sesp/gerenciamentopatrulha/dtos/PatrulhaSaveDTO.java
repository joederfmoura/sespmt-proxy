package br.gov.mt.sesp.gerenciamentopatrulha.dtos;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

public class PatrulhaSaveDTO {

  private Long id;

  @NotNull
  @Size(min = 8, max = 10)
  private String viatura;

  @NotNull
  private List<Long> bairros;

  private List<Long> policiais;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getViatura() {
    return viatura;
  }

  public void setViatura(String viatura) {
    this.viatura = viatura;
  }

  public List<Long> getBairros() {
    return bairros;
  }

  public void setBairros(List<Long> bairros) {
    this.bairros = bairros;
  }

  public List<Long> getPoliciais() {
    return policiais;
  }

  public void setPoliciais(List<Long> policiais) {
    this.policiais = policiais;
  }
}
