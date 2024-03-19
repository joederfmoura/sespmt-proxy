package br.gov.mt.sesp.registrogeral.repositories;

import br.gov.mt.sesp.registrogeral.models.Pessoa;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PessoaRepository extends JpaRepository<Pessoa, Long> {
    @Query("SELECT DISTINCT p FROM Pessoa p "
            + " WHERE (p.cpf = :cpf OR :cpf IS NULL) "
            + " AND LOWER(p.nome) LIKE LOWER(CONCAT('%', :nome, '%')) "
            + " AND LOWER(p.nomeMae) LIKE LOWER(CONCAT('%', :nomeMae, '%')) "
            + " AND LOWER(p.email) LIKE LOWER(CONCAT('%', :email, '%')) "
    )
    Page<Pessoa> findAll(Pageable pageable,
                         @Param("cpf") String cpf,
                         @Param("nome") String nome,
                         @Param("nomeMae") String nomeMae,
                         @Param("email") String email);

    @Query("SELECT DISTINCT p FROM Pessoa p WHERE LOWER(p.email) LIKE LOWER(CONCAT('%', :email, '%')) ")
    List<Pessoa> findByEmail(@Param("email") String email);
}
