package br.gov.mt.sesp.registrogeral.dtos;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.io.Serializable;

public class MensagemDTO implements Serializable {
  
  private String propriedade;
  
  private String mensagem;

  public MensagemDTO(String mensagem) {
    this.mensagem = mensagem;
  }

  public MensagemDTO(String propriedade, String mensagem) {
    this.propriedade = propriedade;
    this.mensagem = mensagem;
  }

  public String getPropriedade() {
    return propriedade;
  }

  public String getMensagem() {
    return mensagem;
  }

  @Override
  public String toString() {
    return ToStringBuilder.reflectionToString(this, ToStringStyle.JSON_STYLE);
  }
}
