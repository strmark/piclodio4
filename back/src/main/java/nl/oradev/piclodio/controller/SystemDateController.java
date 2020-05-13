package nl.oradev.piclodio.controller;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
public class SystemDateController {

    @GetMapping(path = "/systemdate")
    public String getAlarm() {
        ZoneId amsterdam = ZoneId.of("Europe/Amsterdam");
        ZonedDateTime now = ZonedDateTime.of(LocalDateTime.now(), amsterdam);

        return "\"" + now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) + "\"";
    }

}
