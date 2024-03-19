package br.gov.mt.sesp.registrogeral.repositories;

import br.gov.mt.sesp.registrogeral.models.Bairro;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface BairroRepository extends JpaRepository<Bairro, Long> {

    @Query("SELECT DISTINCT b FROM Bairro b "
            + " WHERE b.cidade.id = :idCidade "
            + " AND LOWER(b.nome) LIKE LOWER(CONCAT('%', :nome, '%')) ")
    Page<Bairro> findAll(Pageable pageable,
                         @Param("idCidade") Long idCidade,
                         @Param("nome") String nome);

    @Query("SELECT b FROM Bairro b "
            + " WHERE b.cidade.id = :idCidade "
            + " AND b.id = :id")
    Optional<Bairro> findByCidadeAndId(@Param("idCidade") Long idCidade,
                                       @Param("id") Long id);

}
