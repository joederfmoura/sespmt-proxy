package br.gov.mt.sesp.gerenciamentoprocurado.models;

import java.util.Objects;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import javax.persistence.*;

@Entity
@Table(name = "procurado")
public class Procurado {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Long id;

  @Column(name = "caracteristicas")
  private String caracteristicas;

  @Column(name = "idpessoa")
  private Long idPessoa;

  @Column(name = "idboletimocorrencia")
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

  @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof Procurado)) {
            return false;
        }
        Procurado procurado = (Procurado) o;
        return Objects.equals(id, procurado.id) 
          && Objects.equals(caracteristicas, procurado.caracteristicas) 
          && Objects.equals(idPessoa, procurado.idPessoa) 
          && Objects.equals(idBoletimOcorrencia, procurado.idBoletimOcorrencia);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, caracteristicas, idPessoa, idBoletimOcorrencia);
  }

  @Override
  public String toString() {
    return ToStringBuilder.reflectionToString(this, ToStringStyle.JSON_STYLE);
  }  
}
