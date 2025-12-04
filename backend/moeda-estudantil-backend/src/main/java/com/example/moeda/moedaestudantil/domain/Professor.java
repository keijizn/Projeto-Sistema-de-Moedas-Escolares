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
public class Professor {
  @Id 
  @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
  @NotBlank private String nome;
  @NotBlank 
  @Column(unique = true) private String cpf;
  @Email 
  @NotBlank 
  @Column(unique = true) private String email;
  @NotBlank private String senhaHash;
  private Instant criadoEm = Instant.now();
}
