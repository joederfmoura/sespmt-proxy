package br.gov.mt.sesp.registrogeral.repositories;

import br.gov.mt.sesp.registrogeral.models.Cidade;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CidadeRepository extends JpaRepository<Cidade, Long> {

    @Query("SELECT DISTINCT c FROM Cidade c "
            + " WHERE LOWER(c.uf) LIKE LOWER(CONCAT('%', :uf, '%')) "
            + " AND LOWER(c.nome) LIKE LOWER(CONCAT('%', :nome, '%')) ")
    Page<Cidade> findAll(Pageable pageable,
                         @Param("uf") String uf,
                         @Param("nome") String nome);
}
