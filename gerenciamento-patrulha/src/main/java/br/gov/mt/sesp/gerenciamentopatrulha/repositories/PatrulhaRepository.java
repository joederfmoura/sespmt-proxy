package br.gov.mt.sesp.gerenciamentopatrulha.repositories;

import br.gov.mt.sesp.gerenciamentopatrulha.models.Patrulha;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PatrulhaRepository extends JpaRepository<Patrulha, Long> {

    @Query("SELECT DISTINCT p FROM Patrulha p "
            + " LEFT JOIN p.bairros br "
            + " LEFT JOIN p.policiais pl "
            + " WHERE (p.viatura = :viatura OR :viatura IS NULL) "
            + " AND (br.idBairro = :idBairro OR :idBairro IS NULL) "
            + " AND (pl.idPolicial = :idPolicial OR :idPolicial IS NULL) "
    )
    Page<Patrulha> findAll(Pageable pageable,
                           @Param("viatura") String viatura,
                           @Param("idBairro") Long idBairro,
                           @Param("idPolicial") Long idPolicial);
}
