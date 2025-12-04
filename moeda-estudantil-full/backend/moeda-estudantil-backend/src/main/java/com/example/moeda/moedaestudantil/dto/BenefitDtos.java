package com.example.moeda.moedaestudantil.dto;

import com.example.moeda.moedaestudantil.domain.Benefit;
import jakarta.validation.constraints.*;
import lombok.Getter;

public class BenefitDtos {

  // DTO usado no POST JSON antigo
  public static class Create {
    @NotBlank
    public String titulo;
    public String descricao;
    @Min(1)
    public Integer custo;
  }

  // DTO usado para responder no GET (empresa e público)
  @Getter
  public static class View {
    private Long id;
    private Long empresaId;
    private String titulo;
    private String descricao;
    private Integer custo;
    private boolean ativo;

    public View(Benefit b) {
      this.id = b.getId();
      this.empresaId = b.getEmpresaId();
      this.titulo = b.getTitulo();
      this.descricao = b.getDescricao();
      this.custo = b.getCusto();
      this.ativo = b.isAtivo();
    }
  }
}
