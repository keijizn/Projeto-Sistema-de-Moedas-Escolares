package com.example.moeda.moedaestudantil.dto;

import jakarta.validation.constraints.*;

public class AuthDtos {

  public static class LoginRequest {
    @Email
    @NotBlank
    public String email;

    @NotBlank
    public String senha;

    @NotBlank
    public String role;
  }

  public static class LoginResponse {
    public String role;
    public Long id;
    public String nome;
    public String email;
  }

  public static class AlunoRegister {
    @NotBlank
    public String nome;

    @NotBlank
    public String curso;

    @Email
    @NotBlank
    public String email;

    @NotBlank
    public String senha;

    @NotBlank
    public String cpf;
  }

  public static class ProfessorRegister {
    @NotBlank
    public String nome;

    @NotBlank
    public String cpf;

    @Email
    @NotBlank
    public String email;

    @NotBlank
    public String senha;
  }

  public static class EmpresaRegister {
    @NotBlank
    public String cnpj;

    @NotBlank
    public String nome;

    @Email
    @NotBlank
    public String email;

    @NotBlank
    public String senha;
  }
}
