package br.gov.mt.sesp.carteirafuncional.repositories;

import br.gov.mt.sesp.carteirafuncional.models.Administrativo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AdministrativoRepository extends JpaRepository<Administrativo, Long> {

    @Query("SELECT DISTINCT a FROM Administrativo a "
            + " WHERE LOWER(a.nome) LIKE LOWER(CONCAT('%', :nome, '%')) "
            + " AND LOWER(a.cpf) LIKE LOWER(CONCAT('%', :cpf, '%')) "
            + " AND LOWER(a.matricula) LIKE LOWER(CONCAT('%', :matricula, '%')) "
            + " AND LOWER(a.email) LIKE LOWER(CONCAT('%', :email, '%')) "
    )
    Page<Administrativo> findAll(Pageable pageable,
                                 @Param("nome") String nome,
                                 @Param("cpf") String cpf,
                                 @Param("matricula") String matricula,
                                 @Param("email") String email);
}
