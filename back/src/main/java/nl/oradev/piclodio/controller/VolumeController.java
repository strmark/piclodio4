package nl.oradev.piclodio.controller;

import nl.oradev.piclodio.payload.VolumeRequest;
import nl.oradev.piclodio.util.Audio;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import static nl.oradev.piclodio.util.Audio.getSpeakerOutputVolume;

@CrossOrigin
@RestController
public class VolumeController {

    private static final Logger logger = LoggerFactory.getLogger(VolumeController.class);

    @GetMapping(path = "/volume", produces = "application/json")
    public String getVolume() {
        int volume = (int) Math.ceil( getSpeakerOutputVolume() * 100);
        volume = Math.max(volume, 0);
        volume = Math.min(volume, 100);
        return "{\"volume\":" + volume + "}";
    }

    @PostMapping(path = "/volume", produces = "application/json")
    public String updateVolume(@RequestBody VolumeRequest volume) {
        String volValue = volume.getVolume();
        float vol = Float.parseFloat(volValue) / 100;

        logger.info("Volume {}", vol);
        Audio.setSpeakerOutputVolume(vol);
        return "{\"volume\":" + volValue + "}";
    }
}

