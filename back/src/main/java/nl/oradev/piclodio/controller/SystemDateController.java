package nl.oradev.piclodio.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

import javax.validation.Valid;
import java.util.List;

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
