package com.example.moeda.moedaestudantil.dto;
import jakarta.validation.constraints.*;
public class TransferDtos {
  public static class GrantAluno { @NotNull public Long alunoId; @Min(1) public Integer amount; public String reason; }
  public static class GrantProfessor { @NotNull public Long professorId; @Min(1) public Integer amount; public String reason; }
}
