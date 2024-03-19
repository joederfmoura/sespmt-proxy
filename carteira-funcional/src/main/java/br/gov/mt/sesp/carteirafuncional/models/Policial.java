package br.gov.mt.sesp.carteirafuncional.models;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import javax.persistence.*;

@Entity
@Table(name = "policial")
public class Policial extends Pessoa {

    @ManyToOne
    @JoinColumn(name = "idbatalhao",
            foreignKey = @ForeignKey(name = "policial_idbairro_fkey"))
    private Batalhao batalhao;

    public Batalhao getBatalhao() {
        return batalhao;
    }

    public void setBatalhao(Batalhao batalhao) {
        this.batalhao = batalhao;
    }

    @Override
    public String toString() {
    return ToStringBuilder.reflectionToString(this, ToStringStyle.JSON_STYLE);
  }
}
