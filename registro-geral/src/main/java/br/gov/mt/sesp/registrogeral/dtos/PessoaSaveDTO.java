package br.gov.mt.sesp.registrogeral.dtos;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;

public class PessoaSaveDTO implements Serializable {

  private Long id;
  
  @NotNull
  @Size(min = 2, max = 250)
  private String nome;

  @NotNull
  private String nomeMae;

  private String dataNascimento;

  private String telefones;
  
  private String email;

  private String cpf;

  private String rua;

  private String numero;

  private String complemento;

  private String cep;

  private Long idBairro;

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

  public String getNomeMae() {
    return this.nomeMae;
  }

  public void setNomeMae(String nomeMae) {
    this.nomeMae = nomeMae;
  }

  public String getDataNascimento() {
    return this.dataNascimento;
  }

  public void setDataNascimento(String dataNascimento) {
    this.dataNascimento = dataNascimento;
  }

  public String getTelefones() {
    return this.telefones;
  }

  public void setTelefones(String telefones) {
    this.telefones = telefones;
  }

  public String getEmail() {
    return this.email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getCpf() {
    return this.cpf;
  }

  public void setCpf(String cpf) {
    this.cpf = cpf;
  }

  public String getRua() {
    return this.rua;
  }

  public void setRua(String rua) {
    this.rua = rua;
  }

  public String getNumero() {
    return this.numero;
  }

  public void setNumero(String numero) {
    this.numero = numero;
  }

  public String getComplemento() {
    return this.complemento;
  }

  public void setComplemento(String complemento) {
    this.complemento = complemento;
  }

  public String getCep() {
    return this.cep;
  }

  public void setCep(String cep) {
    this.cep = cep;
  }

  public Long getIdBairro() {
    return this.idBairro;
  }

  public void setIdBairro(Long idBairro) {
    this.idBairro = idBairro;
  }
}