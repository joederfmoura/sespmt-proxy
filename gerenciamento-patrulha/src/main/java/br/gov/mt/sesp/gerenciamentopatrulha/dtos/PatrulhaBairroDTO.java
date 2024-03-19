package br.gov.mt.sesp.gerenciamentopatrulha.dtos;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class PatrulhaBairroDTO {

    private Long id;

    private Long idBairro;

    private String nomeBairro;

    private String cidade;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getIdBairro() {
        return idBairro;
    }

    public void setIdBairro(Long idBairro) {
        this.idBairro = idBairro;
    }

    public String getBairro() {
        return nomeBairro;
    }

    public void setBairro(String nomeBairro) {
        this.nomeBairro = nomeBairro;
    }

    public String getCidade() {
        return cidade;
    }

    public void setCidade(String nomeCidade) {
        this.cidade = cidade;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.JSON_STYLE);

    }
}
