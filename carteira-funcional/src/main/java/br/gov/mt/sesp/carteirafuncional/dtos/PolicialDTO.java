package br.gov.mt.sesp.carteirafuncional.dtos;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class PolicialDTO extends PessoaDTO {

    private BatalhaoDTO batalhao;

    public BatalhaoDTO getBatalhao() {
        return this.batalhao;
    }

    public void setBatalhao(BatalhaoDTO batalhao) {
        this.batalhao = batalhao;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.JSON_STYLE);

    }
}
