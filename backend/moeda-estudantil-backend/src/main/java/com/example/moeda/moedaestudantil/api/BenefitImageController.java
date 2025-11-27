package com.example.moeda.moedaestudantil.api;

import com.example.moeda.moedaestudantil.domain.Benefit;
import com.example.moeda.moedaestudantil.repo.BenefitRepository;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api")
@CrossOrigin
public class BenefitImageController {

    private final BenefitRepository repo;

    public BenefitImageController(BenefitRepository repo) {
        this.repo = repo;
    }

    @GetMapping("/beneficios/{id}/image")
    public ResponseEntity<byte[]> getImage(@PathVariable("id") Long id) {
        // ↑↑↑ AQUI é o ponto importante: @PathVariable("id")

        Optional<Benefit> opt = repo.findById(id);
        if (opt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Benefit b = opt.get();

        if (b.getFoto() == null || b.getFoto().length == 0) {
            return ResponseEntity.notFound().build();
        }

        String ct = b.getFotoContentType();
        if (ct == null || ct.isBlank()) {
            ct = "image/jpeg"; // default
        }

        return ResponseEntity
                .ok()
                .header(HttpHeaders.CONTENT_TYPE, ct)
                .body(b.getFoto());
    }
}
