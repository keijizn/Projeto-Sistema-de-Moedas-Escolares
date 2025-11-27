package com.example.moeda.moedaestudantil.service;
import com.example.moeda.moedaestudantil.domain.*; import com.example.moeda.moedaestudantil.repo.*; import org.springframework.stereotype.Service;
@Service
public class ProfessorService {
  private final ProfessorRepository repo;
  public ProfessorService(ProfessorRepository repo) { this.repo = repo; }
  public Professor get(Long id) { return repo.findById(id).orElseThrow(() -> new IllegalArgumentException("Professor não encontrado")); }
}
