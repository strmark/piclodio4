package nl.oradev.piclodio.controller;

import nl.oradev.piclodio.model.Webradio;
import nl.oradev.piclodio.payload.PlayerRequest;
import nl.oradev.piclodio.repository.WebradioRepository;
import nl.oradev.piclodio.util.VlcPlayer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
public class PlayerController {

    private static Logger logger = LoggerFactory.getLogger(PlayerController.class);
    private VlcPlayer vlcplayer;
    private WebradioRepository webradioRepository;

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
        logger.info("Status: {}", player.getStatus());

        if (Objects.equals(player.getStatus(),"on")) {
            return startPlayer(player.getWebradio(), 0L);
        } else {
            return stopPlayer();
        }
    }

    public String startPlayer(Long webradioId, Long autoStopMinutes) {
        logger.info("Webradio: id = {}", webradioId);
        logger.info("Webradio: autostop = {}", autoStopMinutes);
        String url = null;

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

    private String setDefaultAndSave(Long webradioId,  Webradio webradio) {
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
                .map(Webradio::<String>getUrl)
                .findAny()
                .orElse(null);
    }

    public String startPlayer(String url, Long autoStopMinutes) {
        try {
            // no timer so minutes 0l
            vlcplayer.open(url, autoStopMinutes);
        } catch (Exception exeception) {
            logger.error(exeception.getMessage(), exeception);
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
