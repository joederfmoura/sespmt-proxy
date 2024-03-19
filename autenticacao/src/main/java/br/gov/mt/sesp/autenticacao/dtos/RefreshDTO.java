package br.gov.mt.sesp.autenticacao.dtos;

public class RefreshDTO {

  private String refreshToken;

  public String getRefreshToken() {
    return this.refreshToken;
  }

  public void setRefreshToken(String refreshToken) {
    this.refreshToken = refreshToken;
  }
}
