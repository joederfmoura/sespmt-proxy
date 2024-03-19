package br.gov.mt.sesp.carteirafuncional.repositories;

import br.gov.mt.sesp.carteirafuncional.models.Setor;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SetorRepository extends JpaRepository<Setor, Long> {
}
