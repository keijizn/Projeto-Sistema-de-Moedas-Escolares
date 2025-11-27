package com.example.moeda.moedaestudantil.service;
import com.example.moeda.moedaestudantil.domain.*; import com.example.moeda.moedaestudantil.repo.*; import org.springframework.stereotype.Service; import org.springframework.transaction.annotation.Transactional;
@Service
public class AlunoService {
  private final AlunoRepository repo;
  public AlunoService(AlunoRepository repo) { this.repo = repo; }
  public Aluno get(Long id) { return repo.findById(id).orElseThrow(() -> new IllegalArgumentException("Aluno não encontrado")); }
  @Transactional
  public Aluno update(Long id, String nome, String curso, String email) {
    var a = get(id);
    if (nome != null && !nome.isBlank()) a.setNome(nome);
    if (curso != null && !curso.isBlank()) a.setCurso(curso);
    if (email != null && !email.isBlank()) a.setEmail(email);
    return repo.save(a);
  }
  @Transactional
  public void delete(Long id) { repo.deleteById(id); }
}
