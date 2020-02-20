package nl.oradev.piclodio.controller;


import nl.oradev.piclodio.payload.VolumeRequest;
import nl.oradev.piclodio.util.Audio;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
@RequestMapping("/volume")
public class VolumeController {

    private static final Logger logger = LoggerFactory.getLogger(VolumeController.class);

    private float vol = 0F;

    @RequestMapping(method = RequestMethod.GET, produces = "application/json")
    public String getVolume() {
        int vol = (int)Math.ceil((double) Audio.getSpeakerOutputVolume()*100);
        if (vol < 0)
            vol = 0;
        if (vol > 100)
            vol = 100;
        return "{\"volume\":"+ vol +"}";
    }

    @RequestMapping(method = RequestMethod.POST, produces = "application/json")
    public String updateVolume(@RequestBody VolumeRequest volume ) {
        String volValue = volume.getVolume();

        vol = Float.parseFloat(volValue)/100;
       
        logger.info("Volume {}", vol);
        Audio.setSpeakerOutputVolume(vol);
        return "{\"volume\":"+ volValue +"}";
    }

}

