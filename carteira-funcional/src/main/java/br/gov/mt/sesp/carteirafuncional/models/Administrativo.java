package br.gov.mt.sesp.carteirafuncional.models;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import javax.persistence.*;

@Entity
@Table(name = "administrativo")
public class Administrativo extends Pessoa {

    @ManyToOne
    @JoinColumn(name = "idsetor",
            foreignKey = @ForeignKey(name = "policial_idbairro_fkey"))
    private Setor setor;

    public Setor getSetor() {
        return setor;
    }

    public void setSetor(Setor setor) {
        this.setor = setor;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.JSON_STYLE);
    }
}
