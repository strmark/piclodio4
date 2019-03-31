package nl.oradev.piclodio.repository;

import nl.oradev.piclodio.model.Backup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BackupRepository extends JpaRepository<Backup, Long> {

}
