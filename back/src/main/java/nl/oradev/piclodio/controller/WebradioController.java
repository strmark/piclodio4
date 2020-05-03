package nl.oradev.piclodio.controller;

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

import javax.validation.Valid;
import java.util.List;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
@RequestMapping("/")
public class WebradioController {

    private WebradioRepository webradioRepository;

    public WebradioController(WebradioRepository webradioRepository) {
        this.webradioRepository = webradioRepository;
    }

    @GetMapping("/webradio")
    public List<Webradio> getAllWebradio() {
        return webradioRepository.findAll();
    }

    @PostMapping("/webradio")
    public Webradio createWebradio(@Valid @RequestBody Webradio webradio) {
        return webradioRepository.save(webradio);
    }

    @GetMapping("/webradio/{id}")
    public Webradio getWebradioById(@PathVariable(value = "id") Long webradioId) {
        return webradioRepository.findById(webradioId)
                .orElseThrow(() -> new ResourceNotFoundException("Webradio", "id", webradioId));
    }

    @PutMapping("/webradio/{id}")
    public Webradio updateWebradio(@PathVariable(value = "id") Long webradioId,
                                   @Valid @RequestBody Webradio webradioDetails) {

        Webradio webradio = webradioRepository.findById(webradioId)
                .orElseThrow(() -> new ResourceNotFoundException("Webradio", "id", webradioId));

        webradio.setName(webradioDetails.getName());
        webradio.setUrl(webradioDetails.getUrl());
        webradio.setDefault(webradio.isDefault());

        return webradioRepository.save(webradio);
    }

    @DeleteMapping("/webradio/{id}")
    public ResponseEntity<Long> deleteWebradio(@PathVariable(value = "id") Long webradioId) {
        Webradio webradio = webradioRepository.findById(webradioId)
                .orElseThrow(() -> new ResourceNotFoundException("Webradio", "id", webradioId));

        webradioRepository.delete(webradio);

        return ResponseEntity.ok().build();
    }
}
