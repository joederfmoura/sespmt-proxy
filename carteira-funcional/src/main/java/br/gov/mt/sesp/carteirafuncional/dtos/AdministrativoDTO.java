package br.gov.mt.sesp.carteirafuncional.dtos;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class AdministrativoDTO extends PessoaDTO {

    private SetorDTO setor;

    public SetorDTO getSetor() {
        return this.setor;
    }

    public void setSetor(SetorDTO setor) {
        this.setor = setor;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.JSON_STYLE);

    }
}
