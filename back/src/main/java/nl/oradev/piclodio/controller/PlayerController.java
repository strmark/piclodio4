package nl.oradev.piclodio.controller;

import nl.oradev.piclodio.exception.ResourceNotFoundException;
import nl.oradev.piclodio.job.AlarmJob;
import nl.oradev.piclodio.model.Webradio;
import nl.oradev.piclodio.repository.WebradioRepository;
import nl.oradev.piclodio.payload.PlayerRequest;
import nl.oradev.piclodio.util.VlcPlayer;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URL;
import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
@RequestMapping("/player")
public class PlayerController {

    private VlcPlayer vlcplayer;

    @Autowired
    WebradioRepository webradioRepository;

    public PlayerController(){
        this.vlcplayer = new VlcPlayer();
    }

    @RequestMapping(method = RequestMethod.GET, produces = "application/json")
    public String getPlayer() {
        return "{\"status\":\"on\"}";
    }

    @RequestMapping(method = RequestMethod.POST, produces = "application/json")
    public String updatePlayer(@RequestBody PlayerRequest player) {
        System.out.println("Webradio: "+player.getWebradio());
        System.out.println("Status:" +player.getStatus());

        if (player.getStatus().equals("on"))
           return startPlayer(player.getWebradio(), 0l);
         else
           return stopPlayer();
   }

    public String startPlayer(Long webradioId, Long autoStopMinutes) {
        System.out.println("Webradio: "+webradioId);
        System.out.println("Webradio: "+autoStopMinutes);
        String url = new String("dummy");

        List<Webradio> webradioList = webradioRepository.findAll();

        if (webradioId == null){
            for (Webradio webradio: webradioList) {
                if (webradio.isIs_default())
                    url = new String(webradio.getUrl());
            }
        } else {
            for (Webradio webradio: webradioList) {
                if (webradio.isIs_default() && webradio.getId() == webradioId)
                    url = new String(webradio.getUrl());
                if (webradio.isIs_default() && webradio.getId() != webradioId){
                    webradio.setIs_default(false);
                    webradioRepository.save(webradio);
                }
                if (!webradio.isIs_default() && webradio.getId() == webradioId) {
                    webradio.setIs_default(true);
                    webradioRepository.save(webradio);
                    url = new String(webradio.getUrl());
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
            System.out.println(exeception.getMessage());
        }
        return "{\"status\":\"on\"}";
    }

    public String stopPlayer(){
        // stop playing and return status off
        try {
            this.vlcplayer.close();
        } catch (Exception exception){
            System.out.println(exception.getMessage());
        }
        return "{\"status\":\"off\"}";
    }
}
