package br.gov.mt.sesp.carteirafuncional.repositories;

import br.gov.mt.sesp.carteirafuncional.models.Batalhao;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BatalhaoRepository extends JpaRepository<Batalhao, Long> {
}
