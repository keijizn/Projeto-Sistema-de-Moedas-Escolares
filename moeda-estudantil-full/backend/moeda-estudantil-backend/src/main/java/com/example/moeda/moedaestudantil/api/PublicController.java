package com.example.moeda.moedaestudantil.api;

import com.example.moeda.moedaestudantil.service.BenefitService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@CrossOrigin
public class PublicController {

  private final BenefitService benefits;

  public PublicController(BenefitService benefits) {
    this.benefits = benefits;
  }

  // Usado pela tela do aluno para listar benefícios disponíveis
  @GetMapping("/beneficios")
  public ResponseEntity<?> listActive() {
    return ResponseEntity.ok(benefits.listAllActive());
  }
}
