package nl.oradev.piclodio.controller;

import nl.oradev.piclodio.model.Backup;
import nl.oradev.piclodio.repository.BackupRepository;
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
import java.util.Iterator;
import java.util.List;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
public class BackupController {

    final private static String TEMP_FILE = "/tmp/backup_mp3";

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
        Iterator<String> itr = request.getFileNames();
        MultipartFile file = request.getFile(itr.next());
        String fileName = file.getOriginalFilename();
        File dir = new File(TEMP_FILE);
        if (dir.isDirectory()) {
            File serverFile = new File(dir, fileName);
            BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(serverFile));
            stream.write(file.getBytes());
            stream.close();

        }
        List<Backup> backupList = backupRepository.findAll();
        for (Backup backup : backupList) {
            backupRepository.delete(backup);
        }
        Backup back = new Backup();
        back.setBackupFile("backup_mp3/" + fileName);
        backupRepository.save(back);
        return backupRepository.findAll();
    }

}
