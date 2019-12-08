package nl.oradev.piclodio.controller;

import nl.oradev.piclodio.model.Backup;
import nl.oradev.piclodio.repository.BackupRepository;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.BufferedOutputStream;
import java.util.List;
import java.util.Iterator;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
@RequestMapping("/backup")
public class BackupController {

    private BackupRepository backupRepository;

    public BackupController(BackupRepository backupRepository) {
        this.backupRepository = backupRepository;
    }

    @RequestMapping(method = RequestMethod.GET, produces = "application/json")
    public List<Backup>getBackup() {
        return backupRepository.findAll();
    }

    @RequestMapping(method = RequestMethod.POST)
    public List<Backup> UploadFile(MultipartHttpServletRequest request) throws IOException {
        Iterator<String> itr = request.getFileNames();
        MultipartFile file = request.getFile(itr.next());
        String fileName = file.getOriginalFilename();
        File dir = new File("/tmp/backup_mp3");
        if (dir.isDirectory()) {
            File serverFile = new File(dir, fileName);
            BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(serverFile));
            stream.write(file.getBytes());
            stream.close();
        }
        List<Backup> backupList = backupRepository.findAll();
        for (Backup backup: backupList) {
           backupRepository.delete(backup);
        }
        Backup back = new Backup();
        back.setBackup_file("backup_mp3/" + fileName);
        backupRepository.save(back);
        return backupRepository.findAll();
    }

}
