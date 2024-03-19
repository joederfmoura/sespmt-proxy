package br.gov.mt.sesp.autenticacao.dtos;

public class LoginDTO {

  private String usuario;

  private String senha;

  public String getUsuario() {
    return usuario;
  }

  public void setUsuario(String usuario) {
    this.usuario = usuario;
  }

  public String getSenha() {
    return senha;
  }

  public void setSenha(String senha) {
    this.senha = senha;
  }
}
