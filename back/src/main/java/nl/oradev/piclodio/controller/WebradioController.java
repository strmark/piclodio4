package nl.oradev.piclodio.controller;

import jakarta.validation.Valid;
import nl.oradev.piclodio.dto.WebradioDTO;
import nl.oradev.piclodio.exception.ResourceNotFoundException;
import nl.oradev.piclodio.model.Webradio;
import nl.oradev.piclodio.repository.WebradioRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/")
public class WebradioController {

    private final WebradioRepository webradioRepository;
    private static final String WEBRADIO = "Webradio";

    public WebradioController(WebradioRepository webradioRepository) {
        this.webradioRepository = webradioRepository;
    }

    @GetMapping("/webradio/")
    public List<Webradio> getAllWebradio() {
        return webradioRepository.findAll();
    }

    @PostMapping("/webradio/")
    public Webradio createWebradio(@Valid @RequestBody WebradioDTO webradioDTO) {
        var webradio = new Webradio();
        if (webradioDTO.id() != null) {
            webradio.setId(webradioDTO.id());
        }
        webradio.setName(webradioDTO.name());
        webradio.setDefault(webradioDTO.isDefault());
        webradio.setUrl(webradioDTO.url());
        return webradioRepository.save(webradio);
    }

    @GetMapping("/webradio/{id}")
    public Webradio getWebradioById(@PathVariable(value = "id") Long webradioId) {
        return webradioRepository.findById(webradioId)
                .orElseThrow(() -> new ResourceNotFoundException(WEBRADIO, "id", webradioId));
    }

    @PutMapping("/webradio/{id}")
    public Webradio updateWebradio(@PathVariable(value = "id") Long webradioId,
                                   @Valid @RequestBody WebradioDTO webradioDTO) {
        var webradio = webradioRepository.findById(webradioId)
                .orElseThrow(() -> new ResourceNotFoundException(WEBRADIO, "id", webradioId));

        webradio.setName(webradioDTO.name());
        webradio.setUrl(webradioDTO.url());
        webradio.setDefault(webradioDTO.isDefault());
        return webradioRepository.save(webradio);
    }

    @DeleteMapping("/webradio/{id}")
    public ResponseEntity<Long> deleteWebradio(@PathVariable(value = "id") Long webradioId) {
        var webradio = webradioRepository.findById(webradioId)
                .orElseThrow(() -> new ResourceNotFoundException(WEBRADIO, "id", webradioId));

        webradioRepository.delete(webradio);
        return ResponseEntity.ok().build();
    }
}
