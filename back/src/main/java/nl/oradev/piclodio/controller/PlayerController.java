package nl.oradev.piclodio.controller;

import nl.oradev.piclodio.model.Webradio;
import nl.oradev.piclodio.payload.PlayerRequest;
import nl.oradev.piclodio.repository.WebradioRepository;
import nl.oradev.piclodio.util.VlcPlayer;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
@RequestMapping("/player")
public class PlayerController {

    private static Logger logger = LoggerFactory.getLogger(PlayerController.class);

    private VlcPlayer vlcplayer;

    private WebradioRepository webradioRepository;

    public PlayerController(WebradioRepository webradioRepository){
        this.webradioRepository = webradioRepository;
        this.vlcplayer = new VlcPlayer();
    }

    @RequestMapping(method = RequestMethod.GET, produces = "application/json")
    public String getPlayer() {
        return "{\"status\":\"on\"}";
    }

    @RequestMapping(method = RequestMethod.POST, produces = "application/json")
    public String updatePlayer(@RequestBody PlayerRequest player) {
        logger.info( "Webradio: {}", player.getWebradio());
        logger.info( "Status: {}" , player.getStatus());

        if (player.getStatus().equals("on"))
           return startPlayer(player.getWebradio(), 0L);
         else
           return stopPlayer();
   }

    public String startPlayer(Long webradioId, Long autoStopMinutes) {
        logger.info( "Webradio: {}", webradioId);
        logger.info( "Webradio: {}", autoStopMinutes);
        String url = "dummy";

        List<Webradio> webradioList = webradioRepository.findAll();

        if (webradioId == null){
            for (Webradio webradio: webradioList) {
                if (webradio.isIs_default())
                    url = webradio.getUrl();
            }
        } else {
            for (Webradio webradio: webradioList) {
                if (webradio.isIs_default() && webradio.getId().equals(webradioId))
                    url = webradio.getUrl();
                if (webradio.isIs_default() && !webradio.getId().equals(webradioId)){
                    webradio.setIs_default(false);
                    webradioRepository.save(webradio);
                }
                if (!webradio.isIs_default() && webradio.getId().equals(webradioId)) {
                    webradio.setIs_default(true);
                    webradioRepository.save(webradio);
                    url = webradio.getUrl();
                }
            }
        }
        return startPlayer(url, autoStopMinutes);
    }
    
    public String startPlayer(String url, Long autoStopMinutes){
        try {
            // no timer so minutes 0l
            this.vlcplayer.open(url, autoStopMinutes);
        } catch (Exception exeception) {
            logger.error(exeception.getMessage(), exeception);
        }
        return "{\"status\":\"on\"}";
    }

    public String stopPlayer(){
        // stop playing and return status off
        try {
            this.vlcplayer.close();
        } catch (Exception exception){
            logger.error(exception.getMessage(), exception);
        }
        return "{\"status\":\"off\"}";
    }
}
