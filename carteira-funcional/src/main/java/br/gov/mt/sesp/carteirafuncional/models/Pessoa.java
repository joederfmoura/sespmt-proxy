package br.gov.mt.sesp.carteirafuncional.models;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.Objects;

@MappedSuperclass
public abstract class Pessoa {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Long id;

  @Column(name = "matricula")
  private String matricula;

  @Column(name = "nome")
  private String nome;

  @Column(name = "nomemae")
  private String nomeMae;

  @Column(name = "datanasc")
  private LocalDate dataNascimento;

  @Column(name = "telefones")
  private String telefones;

  @Column(name = "email")
  private String email;

  @Column(name = "cpf")
  private String cpf;

  @Column(name = "idbairro")
  private Long idBairro;

  @Column(name = "rua")
  private String rua;

  @Column(name = "numero")
  private String numero;

  @Column(name = "complemento")
  private String complemento;

  @Column(name = "cep")
  private String cep;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getMatricula() {
    return matricula;
  }

  public void setMatricula(String matricula) {
    this.matricula = matricula;
  }

  public String getNome() {
    return nome;
  }

  public void setNome(String nome) {
    this.nome = nome;
  }

  public String getNomeMae() {
    return nomeMae;
  }

  public void setNomeMae(String nomeMae) {
    this.nomeMae = nomeMae;
  }

  public LocalDate getDataNascimento() {
    return dataNascimento;
  }

  public void setDataNascimento(LocalDate dataNascimento) {
    this.dataNascimento = dataNascimento;
  }

  public String getTelefones() {
    return telefones;
  }

  public void setTelefones(String telefones) {
    this.telefones = telefones;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getCpf() {
    return cpf;
  }

  public void setCpf(String cpf) {
    this.cpf = cpf;
  }

  public Long getIdBairro() {
    return idBairro;
  }

  public void setIdBairro(Long idBairro) {
    this.idBairro = idBairro;
  }

  public String getRua() {
    return rua;
  }

  public void setRua(String rua) {
    this.rua = rua;
  }

  public String getNumero() {
    return numero;
  }

  public void setNumero(String numero) {
    this.numero = numero;
  }

  public String getComplemento() {
    return complemento;
  }

  public void setComplemento(String complemento) {
    this.complemento = complemento;
  }

  public String getCep() {
    return cep;
  }

  public void setCep(String cep) {
    this.cep = cep;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Pessoa pessoa = (Pessoa) o;
    return Objects.equals(id, pessoa.id) && Objects.equals(matricula, pessoa.matricula) && Objects.equals(nome, pessoa.nome) && Objects.equals(nomeMae, pessoa.nomeMae) && Objects.equals(dataNascimento, pessoa.dataNascimento) && Objects.equals(telefones, pessoa.telefones) && Objects.equals(email, pessoa.email) && Objects.equals(cpf, pessoa.cpf) && Objects.equals(idBairro, pessoa.idBairro) && Objects.equals(rua, pessoa.rua) && Objects.equals(numero, pessoa.numero) && Objects.equals(complemento, pessoa.complemento) && Objects.equals(cep, pessoa.cep);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, matricula, nome, nomeMae, dataNascimento, telefones, email, cpf, idBairro, rua, numero, complemento, cep);
  }
}
