package br.gov.mt.sesp.carteirafuncional.repositories;

import br.gov.mt.sesp.carteirafuncional.models.Policial;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PolicialRepository extends JpaRepository<Policial, Long> {

    @Query("SELECT DISTINCT p FROM Policial p "
            + " WHERE LOWER(p.nome) LIKE LOWER(CONCAT('%', :nome, '%')) "
            + " AND LOWER(p.cpf) LIKE LOWER(CONCAT('%', :cpf, '%')) "
            + " AND LOWER(p.matricula) LIKE LOWER(CONCAT('%', :matricula, '%')) "
            + " AND LOWER(p.email) LIKE LOWER(CONCAT('%', :email, '%')) "
    )
    Page<Policial> findAll(Pageable pageable,
                                 @Param("nome") String nome,
                                 @Param("cpf") String cpf,
                                 @Param("matricula") String matricula,
                                 @Param("email") String email);
}
