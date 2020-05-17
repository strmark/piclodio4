package nl.oradev.piclodio.controller;

import nl.oradev.piclodio.model.Backup;
import nl.oradev.piclodio.repository.BackupRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
public class BackupController {
    private static final Logger logger = LoggerFactory.getLogger(BackupController.class);
    private static final String TEMP_FILE = "/tmp/backup_mp3";
    private BackupRepository backupRepository;

    public BackupController(BackupRepository backupRepository) {
        this.backupRepository = backupRepository;
    }

    @GetMapping(path = "/backup", produces = "application/json")
    public List<Backup> getBackup() {
        return backupRepository.findAll();
    }

    @PostMapping(path = "/backup")
    public List<Backup> uploadFile(MultipartHttpServletRequest request) throws IOException {
        MultipartFile file = request.getFile(request.getFileNames().next());
        String fileName = file.getOriginalFilename();
        File dir = new File(TEMP_FILE);
        logger.info("Writing file");
        if (dir.isDirectory()) {
            File serverFile = new File(dir, fileName);
            logger.info("Writing to file {}", fileName);
            try (BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(serverFile))) {
                stream.write(file.getBytes());
            }
        }

        backupRepository
                .findAll()
                .stream()
                .forEach(backup -> backupRepository.delete(backup));

        Backup backup = new Backup();
        backup.setBackupFile("backup_mp3/" + fileName);
        backupRepository.save(backup);
        return backupRepository.findAll();
    }
}
