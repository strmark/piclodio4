package nl.oradev.piclodio.controller;

import nl.oradev.piclodio.payload.VolumeRequest;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import nl.oradev.piclodio.util.Audio;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
@RequestMapping("/volume")
public class VolumeController {

    private float vol = 0F;

    @RequestMapping(method = RequestMethod.GET, produces = "application/json")
    public String getVolume() {
        Audio audio = new Audio();
        int vol = (int)Math.ceil((double)audio.getSpeakerOutputVolume()*100);
        if (vol < 0)
            vol = 0;
        if (vol > 100)
            vol = 100;
        return "{\"volume\":"+ vol +"}";
    }

    @RequestMapping(method = RequestMethod.POST, produces = "application/json")
    public String updateVolume(@RequestBody VolumeRequest volume )throws Exception {
        String volValue = volume.getVolume();

        vol = Float.valueOf(volValue)/100;
       
        System.out.println(vol);
        Audio audio = new Audio();
        audio.setSpeakerOutputVolume(vol);
        return "{\"volume\":"+ volValue +"}";
    }

}

