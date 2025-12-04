package com.example.moeda.moedaestudantil.service;

import com.example.moeda.moedaestudantil.domain.Benefit;
import com.example.moeda.moedaestudantil.dto.BenefitDtos.Create;
import com.example.moeda.moedaestudantil.dto.BenefitDtos.View;
import com.example.moeda.moedaestudantil.repo.BenefitRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class BenefitService {

  private final BenefitRepository repo;

  public BenefitService(BenefitRepository repo) {
    this.repo = repo;
  }

  // POST JSON antigo (sem foto)
  @Transactional
  public Long create(Long empresaId, Create dto) {
    Benefit b = Benefit.builder()
        .empresaId(empresaId)
        .titulo(dto.titulo)
        .descricao(dto.descricao)
        .custo(dto.custo)
        .ativo(true)
        .build();
    return repo.save(b).getId();
  }

  // POST multipart com foto
  @Transactional
  public Long createWithImage(Long empresaId,
                              String titulo,
                              String descricao,
                              Integer custo,
                              byte[] foto,
                              String contentType) {

    Benefit b = Benefit.builder()
        .empresaId(empresaId)
        .titulo(titulo)
        .descricao(descricao)
        .custo(custo)
        .ativo(true)
        .build();

    if (foto != null && foto.length > 0) {
      b.setFoto(foto);
      b.setFotoContentType(contentType);
    }

    return repo.save(b).getId();
  }

  // Lista somente ativos para os alunos
  public List<View> listAllActive() {
    return repo.findByAtivoTrue()
        .stream()
        .map(View::new)
        .toList();
  }

  // Lista por empresa para a tela da empresa parceira
  public List<View> listByEmpresa(Long empresaId) {
    return repo.findByEmpresaId(empresaId)
        .stream()
        .map(View::new)
        .toList();
  }
}
