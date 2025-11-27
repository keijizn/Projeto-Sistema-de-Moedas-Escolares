package com.example.moeda.moedaestudantil.service;
import com.example.moeda.moedaestudantil.domain.*; import com.example.moeda.moedaestudantil.dto.AuthDtos.*; import com.example.moeda.moedaestudantil.repo.*;
import org.springframework.stereotype.Service; import org.springframework.transaction.annotation.Transactional;
@Service
public class AuthService {
  private final AlunoRepository alunoRepo; private final ProfessorRepository professorRepo; private final EmpresaParceiraRepository empRepo; private final PasswordService password;
  public AuthService(AlunoRepository alunoRepo, ProfessorRepository professorRepo, EmpresaParceiraRepository empRepo, PasswordService password) {
    this.alunoRepo = alunoRepo; this.professorRepo = professorRepo; this.empRepo = empRepo; this.password = password;
  }
  @Transactional
  public Long registerAluno(AlunoRegister dto) {
    if (alunoRepo.existsByEmail(dto.email)) throw new IllegalArgumentException("Email já cadastrado");
    if (alunoRepo.existsByCpf(dto.cpf)) throw new IllegalArgumentException("CPF já cadastrado");
    var a = alunoRepo.save(Aluno.builder().nome(dto.nome).curso(dto.curso).email(dto.email).cpf(dto.cpf).senhaHash(password.hash(dto.senha)).build());
    return a.getId();
  }
  @Transactional
  public Long registerProfessor(ProfessorRegister dto) {
    if (professorRepo.existsByEmail(dto.email)) throw new IllegalArgumentException("Email já cadastrado");
    if (professorRepo.existsByCpf(dto.cpf)) throw new IllegalArgumentException("CPF já cadastrado");
    var p = professorRepo.save(Professor.builder().nome(dto.nome).cpf(dto.cpf).email(dto.email).senhaHash(password.hash(dto.senha)).build());
    return p.getId();
  }
  @Transactional
  public Long registerEmpresa(EmpresaRegister dto) {
    if (empRepo.existsByEmail(dto.email)) throw new IllegalArgumentException("Email já cadastrado");
    if (empRepo.existsByCnpj(dto.cnpj)) throw new IllegalArgumentException("CNPJ já cadastrado");
    var e = empRepo.save(EmpresaParceira.builder().cnpj(dto.cnpj).nome(dto.nome).email(dto.email).senhaHash(password.hash(dto.senha)).build());
    return e.getId();
  }
  public LoginResponse login(LoginRequest dto) {
    var r = new LoginResponse();
    switch (dto.role.toUpperCase()) {
      case "ALUNO" -> {
        var a = alunoRepo.findByEmail(dto.email).orElseThrow(() -> new IllegalArgumentException("Credenciais inválidas"));
        if (!password.matches(dto.senha, a.getSenhaHash())) throw new IllegalArgumentException("Credenciais inválidas");
        r.role = "ALUNO"; r.id = a.getId(); r.nome = a.getNome(); r.email = a.getEmail();
      }
      case "PROFESSOR" -> {
        var p = professorRepo.findByEmail(dto.email).orElseThrow(() -> new IllegalArgumentException("Credenciais inválidas"));
        if (!password.matches(dto.senha, p.getSenhaHash())) throw new IllegalArgumentException("Credenciais inválidas");
        r.role = "PROFESSOR"; r.id = p.getId(); r.nome = p.getNome(); r.email = p.getEmail();
      }
      case "EMPRESA" -> {
        var e = empRepo.findByEmail(dto.email).orElseThrow(() -> new IllegalArgumentException("Credenciais inválidas"));
        if (!password.matches(dto.senha, e.getSenhaHash())) throw new IllegalArgumentException("Credenciais inválidas");
        r.role = "EMPRESA"; r.id = e.getId(); r.nome = e.getNome(); r.email = e.getEmail();
      }
      default -> throw new IllegalArgumentException("Role inválida");
    }
    return r;
  }
}
