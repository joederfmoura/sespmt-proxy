package br.gov.mt.sesp.gerenciamentoocorrencia.models;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "suspeito")
public class Suspeito {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "caracteristicas")
    private String caracteristicas;

    @Column(name = "idpessoa")
    private Long idPessoa;

    @ManyToOne
    @JoinColumn(name = "idboletimocorrencia",
            foreignKey = @ForeignKey(name = "suspeito_idboletimocorrencia_fkey"))
    private BoletimOcorrencia boletimOcorrencia;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCaracteristicas() {
        return caracteristicas;
    }

    public void setCaracteristicas(String caracteristicas) {
        this.caracteristicas = caracteristicas;
    }

    public Long getIdPessoa() {
        return idPessoa;
    }

    public void setIdPessoa(Long idPessoa) {
        this.idPessoa = idPessoa;
    }

    public BoletimOcorrencia getBoletimOcorrencia() {
        return boletimOcorrencia;
    }

    public void setBoletimOcorrencia(BoletimOcorrencia boletimOcorrencia) {
        this.boletimOcorrencia = boletimOcorrencia;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Suspeito suspeito = (Suspeito) o;
        return Objects.equals(id, suspeito.id) && Objects.equals(caracteristicas, suspeito.caracteristicas) && Objects.equals(idPessoa, suspeito.idPessoa) && Objects.equals(boletimOcorrencia, suspeito.boletimOcorrencia);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, caracteristicas, idPessoa, boletimOcorrencia);
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.JSON_STYLE);
    }
}
