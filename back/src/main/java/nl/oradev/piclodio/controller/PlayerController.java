package nl.oradev.piclodio.controller;

import nl.oradev.piclodio.model.Webradio;
import nl.oradev.piclodio.payload.PlayerRequest;
import nl.oradev.piclodio.repository.WebradioRepository;
import nl.oradev.piclodio.util.VlcPlayer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
public class PlayerController {

    private static Logger logger = LoggerFactory.getLogger(PlayerController.class);

    private VlcPlayer vlcplayer;

    private WebradioRepository webradioRepository;

    public PlayerController(WebradioRepository webradioRepository) {
        this.webradioRepository = webradioRepository;
        this.vlcplayer = new VlcPlayer();
    }

    @GetMapping(path = "/player", produces = "application/json")
    public String getPlayer() {
        return "{\"status\":\"on\"}";
    }

    @PostMapping(path = "/player", produces = "application/json")
    public String updatePlayer(@RequestBody PlayerRequest player) {
        logger.info("Webradio: {}", player.getWebradio());
        logger.info("Status: {}", player.getStatus());

        if (player.getStatus().equals("on")) {
            return startPlayer(player.getWebradio(), 0L);
        } else {
            return stopPlayer();
        }
    }

    public String startPlayer(Long webradioId, Long autoStopMinutes) {
        logger.info("Webradio: id = {}", webradioId);
        logger.info("Webradio: autostop = {}", autoStopMinutes);
        String url = "dummy";

        List<Webradio> webradioList = webradioRepository.findAll();

        if (webradioId == null) {
            url = getWebradioUrl(webradioList);
        } else {
            for (Webradio webradio : webradioList) {
                if (webradio.isDefault()) {
                    if (webradio.getId().equals(webradioId)) {
                        webradio.setDefault(true);
                        webradioRepository.save(webradio);
                        url = webradio.getUrl();
                    } else {
                        webradio.setDefault(false);
                        webradioRepository.save(webradio);
                    }
                } else if (webradio.getId().equals(webradioId)) {
                    webradio.setDefault(true);
                    webradioRepository.save(webradio);
                    url = webradio.getUrl();
                }
            }
        }
        return startPlayer(url, autoStopMinutes);
    }

    private String getWebradioUrl(List<Webradio> webradioList) {
        return webradioList
                .stream()
                .filter(Webradio::isDefault)
                .map(Webradio::<String>getUrl)
                .findAny()                                     // If 'findAny' then return found
                .orElse(null);
    }

    public String startPlayer(String url, Long autoStopMinutes) {
        try {
            // no timer so minutes 0l
            this.vlcplayer.open(url, autoStopMinutes);
        } catch (Exception exeception) {
            logger.error(exeception.getMessage(), exeception);
        }
        return "{\"status\":\"on\"}";
    }

    public String stopPlayer() {
        // stop playing and return status off
        try {
            this.vlcplayer.close();
        } catch (Exception exception) {
            logger.error(exception.getMessage(), exception);
        }
        return "{\"status\":\"off\"}";
    }
}
