package com.example.moeda.moedaestudantil.domain;
import jakarta.persistence.*; 
import jakarta.validation.constraints.*; 
import lombok.*; 
import java.time.Instant;
@Entity 
@Getter 
@Setter 
@NoArgsConstructor 
@AllArgsConstructor 
@Builder
public class Aluno {
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
  @NotBlank private String nome;
  @NotBlank private String curso;
  @Email @NotBlank @Column(unique = true) private String email;
  @NotBlank private String senhaHash;
  @NotBlank @Column(unique = true) private String cpf;
  private Instant criadoEm = Instant.now();
}
