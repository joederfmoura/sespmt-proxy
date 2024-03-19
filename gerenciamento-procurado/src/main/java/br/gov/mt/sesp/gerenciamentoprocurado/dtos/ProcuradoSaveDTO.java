package br.gov.mt.sesp.gerenciamentoprocurado.dtos;

public class ProcuradoSaveDTO {

  private Long id;

  private String caracteristicas;

  private Long idPessoa;

  private Long idBoletimOcorrencia;

  public Long getId() {
    return this.id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getCaracteristicas() {
    return this.caracteristicas;
  }

  public void setCaracteristicas(String caracteristicas) {
    this.caracteristicas = caracteristicas;
  }

  public Long getIdPessoa() {
    return this.idPessoa;
  }

  public void setIdPessoa(Long idPessoa) {
    this.idPessoa = idPessoa;
  }

  public Long getIdBoletimOcorrencia() {
    return this.idBoletimOcorrencia;
  }

  public void setIdBoletimOcorrencia(Long idBoletimOcorrencia) {
    this.idBoletimOcorrencia = idBoletimOcorrencia;
  }
  
}
