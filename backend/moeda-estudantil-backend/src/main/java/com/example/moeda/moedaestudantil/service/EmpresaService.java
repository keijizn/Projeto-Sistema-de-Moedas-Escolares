package com.example.moeda.moedaestudantil.service;
import com.example.moeda.moedaestudantil.domain.*; import com.example.moeda.moedaestudantil.repo.*; import org.springframework.stereotype.Service;
@Service
public class EmpresaService {
  private final EmpresaParceiraRepository repo;
  public EmpresaService(EmpresaParceiraRepository repo) { this.repo = repo; }
  public EmpresaParceira get(Long id) { return repo.findById(id).orElseThrow(() -> new IllegalArgumentException("Empresa não encontrada")); }
}
