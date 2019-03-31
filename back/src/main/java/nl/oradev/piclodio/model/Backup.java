package nl.oradev.piclodio.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "backups")
@EntityListeners(AuditingEntityListener.class)
@JsonIgnoreProperties(value = {"createdAt", "updatedAt"},
        allowGetters = true)
public class Backup {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "backup_id")
    private Long id;

    @NotBlank
    private String backup_file;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBackup_file() {
        return backup_file;
    }

    public void setBackup_file(String backup_file) {
        this.backup_file = backup_file;
    }


}
