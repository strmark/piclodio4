package nl.oradev.piclodio.repository;

import nl.oradev.piclodio.model.Webradio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WebradioRepository extends JpaRepository<Webradio, Long> {

}
