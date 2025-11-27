package com.example.moeda.moedaestudantil.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import com.example.moeda.moedaestudantil.dto.AuthDtos.*;
import com.example.moeda.moedaestudantil.service.AuthService;
import com.example.moeda.moedaestudantil.service.MailService;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Objects;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin
public class AuthController {

  private final AuthService service;
  private final MailService mail;

  public AuthController(AuthService service, MailService mail) {
    this.service = service;
    this.mail = mail;
  }

  private static String readStringByMethods(Object src, String... candidates) {
    if (src == null) return null;
    for (String mName : candidates) {
      try {
        Method m = src.getClass().getMethod(mName);
        Object v = m.invoke(src);
        if (v instanceof String s && !s.isBlank()) return s;
      } catch (NoSuchMethodException ignored) {
      } catch (Exception e) {
      }
    }
    return null;
  }

  private static String readStringByFields(Object src, String... candidates) {
    if (src == null) return null;
    for (String fName : candidates) {
      try {
        Field f = src.getClass().getField(fName);
        Object v = f.get(src);
        if (v instanceof String s && !s.isBlank()) return s;
      } catch (NoSuchFieldException ignored) {
      } catch (Exception e) {
      }
    }
    return null;
  }

  private static String readStringSmart(Object src, String base) {
    if (src == null) return null;
    String val =
        readStringByMethods(src, "get" + capitalize(base), base) ;
    if (val == null) val = readStringByFields(src, base);
    return val;
  }

  private static String capitalize(String s) {
    return (s == null || s.isEmpty()) ? s : Character.toUpperCase(s.charAt(0)) + s.substring(1);
  }

  private static String coalesce(String... vals) {
    for (String v : vals) if (v != null && !v.isBlank()) return v;
    return null;
  }

  @PostMapping("/aluno/register")
  public ResponseEntity<?> cadAluno(@Valid @RequestBody AlunoRegister dto) {
    var result = service.registerAluno(dto);

    String nome  = coalesce(readStringSmart(result, "nome"),  readStringSmart(dto, "nome"));
    String email = coalesce(readStringSmart(result, "email"), readStringSmart(dto, "email"));

    if (email != null && nome != null) {
      String subject = "Cadastro confirmado – Moedas Escolares (Aluno)";
      String html = """
          <h2>Olá, %s!</h2>
          <p>Seu cadastro como <strong>Aluno</strong> foi concluído com sucesso.</p>
          <p>Agora você já pode acessar o sistema, acompanhar seu saldo e resgatar benefícios.</p>
          <p>Bons estudos!<br>Equipe Moedas Escolares</p>
          """.formatted(nome);
      mail.sendHtml(email, subject, html);
    }

    return ResponseEntity.ok(result);
  }

  @PostMapping("/professor/register")
  public ResponseEntity<?> cadProfessor(@Valid @RequestBody ProfessorRegister dto) {
    var result = service.registerProfessor(dto);

    String nome  = coalesce(readStringSmart(result, "nome"),  readStringSmart(dto, "nome"));
    String email = coalesce(readStringSmart(result, "email"), readStringSmart(dto, "email"));

    if (email != null && nome != null) {
      String subject = "Cadastro confirmado – Moedas Escolares (Professor)";
      String html = """
          <h2>Olá, %s!</h2>
          <p>Seu cadastro como <strong>Professor</strong> foi concluído com sucesso.</p>
          <p>Você já pode distribuir moedas aos alunos e acompanhar o histórico.</p>
          <p>Abraços,<br>Equipe Moedas Escolares</p>
          """.formatted(nome);
      mail.sendHtml(email, subject, html);
    }

    return ResponseEntity.ok(result);
  }

  @PostMapping("/empresa/register")
  public ResponseEntity<?> cadEmpresa(@Valid @RequestBody EmpresaRegister dto) {
    var result = service.registerEmpresa(dto);

    String nome  = coalesce(readStringSmart(result, "nome"),  readStringSmart(dto, "nome"));
    String email = coalesce(readStringSmart(result, "email"), readStringSmart(dto, "email"));

    if (email != null && nome != null) {
      String subject = "Cadastro confirmado – Moedas Escolares (Empresa Parceira)";
      String html = """
          <h2>Olá, %s!</h2>
          <p>Seu cadastro como <strong>Empresa Parceira</strong> foi concluído com sucesso.</p>
          <p>Você já pode criar benefícios e distribuir moedas para professores.</p>
          <p>Bem-vindos!<br>Equipe Moedas Escolares</p>
          """.formatted(nome);
      mail.sendHtml(email, subject, html);
    }

    return ResponseEntity.ok(result);
  }

  @PostMapping("/login")
  public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest dto) {
    return ResponseEntity.ok(service.login(dto));
  }
}
