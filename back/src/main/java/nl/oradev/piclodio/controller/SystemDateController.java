package nl.oradev.piclodio.controller;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
@RequestMapping("/systemdate")
public class SystemDateController {

        @RequestMapping(method = RequestMethod.GET)
        public String getAlarm() {
            ZoneId amsterdam = ZoneId.of("Europe/Amsterdam");
            ZonedDateTime now = ZonedDateTime.of(LocalDateTime.now(), amsterdam);

            return "\"" + now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) + "\"";
        }

}
