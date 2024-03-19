package br.gov.mt.sesp.gerenciamentopatrulha.dtos;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class PatrulhaPolicialDTO {

    private Long id;

    private String policial;

    private Long idPolicial;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNomePOlicial() {
        return policial;
    }

    public void setNomePolicial(String policial) {
        this.policial = policial;
    }

    public Long getIdPolicial() {
        return idPolicial;
    }

    public void setIdPolicial(Long idPolicial) {
        this.idPolicial = idPolicial;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.JSON_STYLE);

    }
}
