package nl.oradev.piclodio.controller;

import nl.oradev.piclodio.model.Webradio;
import nl.oradev.piclodio.payload.PlayerRequest;
import nl.oradev.piclodio.repository.WebradioRepository;
import nl.oradev.piclodio.util.VlcPlayer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
public class PlayerController {

    private static final Logger logger = LoggerFactory.getLogger(PlayerController.class);
    private final VlcPlayer vlcplayer;
    private final WebradioRepository webradioRepository;

    public PlayerController(WebradioRepository webradioRepository, VlcPlayer vlcPlayer) {
        this.webradioRepository = webradioRepository;
        this.vlcplayer = vlcPlayer;
    }

    @GetMapping(path = "/player", produces = "application/json")
    public String getPlayer() {
        return "{\"status\":\"on\"}";
    }

    @PostMapping(path = "/player", produces = "application/json")
    public String updatePlayer(@RequestBody PlayerRequest player) {
        logger.info("Webradio: {}", player.getWebradio());

        if (Objects.equals(player.getStatus(), "on")) {
            logger.info("Status: on");
            return startPlayer(player.getWebradio(), 0L);
        } else {
            logger.info("Status: off");
            return stopPlayer();
        }
    }

    public String startPlayer(Long webradioId, Long autoStopMinutes) {
        logger.info("Webradio: id = {}", webradioId);
        logger.info("Webradio: autostop = {}", autoStopMinutes);
        String url;

        if (webradioId == null) {
            url = getWebradioUrl(webradioRepository.findAll());
        } else {
            url = webradioRepository
                    .findAll()
                    .stream()
                    .map(webradio -> setDefaultAndSave(webradioId, webradio))
                    .filter(Objects::nonNull)
                    .collect(Collectors.joining());
        }
        return startPlayer(url, autoStopMinutes);
    }

    private String setDefaultAndSave(Long webradioId, Webradio webradio) {
        if (webradio.isDefault()) {
            if (Objects.equals(webradio.getId(), webradioId)) {
                webradio.setDefault(true);
                webradioRepository.save(webradio);
                return webradio.getUrl();
            } else {
                webradio.setDefault(false);
                webradioRepository.save(webradio);
            }
        } else if (Objects.equals(webradio.getId(), webradioId)) {
            webradio.setDefault(true);
            webradioRepository.save(webradio);
            return webradio.getUrl();
        }
        return null;
    }

    private String getWebradioUrl(List<Webradio> webradioList) {
        return webradioList
                .stream()
                .filter(Webradio::isDefault)
                .map(Webradio::getUrl)
                .findAny()
                .orElse(null);
    }

    public String startPlayer(String url, Long autoStopMinutes) {
        try {
            // no timer so minutes 0l
            vlcplayer.open(url, autoStopMinutes);
        } catch (InterruptedException | IOException exeception) {
            logger.error(exeception.getMessage(), exeception);
            Thread.currentThread().interrupt();
        }
        return "{\"status\":\"on\"}";
    }

    public String stopPlayer() {
        // stop playing and return status off
        try {
            vlcplayer.close();
        } catch (Exception exception) {
            logger.error(exception.getMessage(), exception);
        }
        return "{\"status\":\"off\"}";
    }
}
