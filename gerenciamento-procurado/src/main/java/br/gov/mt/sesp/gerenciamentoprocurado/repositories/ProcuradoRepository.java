package br.gov.mt.sesp.gerenciamentoprocurado.repositories;

import java.util.*;
import java.util.stream.Collectors;

import br.gov.mt.sesp.gerenciamentoprocurado.models.Procurado;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProcuradoRepository extends JpaRepository<Procurado, Long> {

  @Query("SELECT DISTINCT p FROM Procurado p "
          + " WHERE LOWER(p.caracteristicas) LIKE LOWER(CONCAT('%', :caracteristicas, '%')) "
          + " AND (p.idPessoa = :idPessoa OR :idPessoa IS NULL) "
          + " AND (p.idBoletimOcorrencia = :idBoletimOcorrencia OR :idBoletimOcorrencia IS NULL) "
  )
  Page<Procurado> findAll(Pageable pageable,
                          @Param("caracteristicas") String caracteristicas,
                          @Param("idPessoa") Long idPessoa,
                          @Param("idBoletimOcorrencia") Long idBoletimOcorrencia);

  List<Procurado> findAllByIdBoletimOcorrencia(@Param("idBoletimOcorrencia") Long idBoletimOcorrencia);

}
