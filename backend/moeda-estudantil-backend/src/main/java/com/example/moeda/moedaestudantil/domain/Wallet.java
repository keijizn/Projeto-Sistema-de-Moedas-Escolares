package com.example.moeda.moedaestudantil.domain;
import jakarta.persistence.*; 
import lombok.*; 
import java.math.BigDecimal;
@Entity 
@Getter 
@Setter 
@NoArgsConstructor 
@AllArgsConstructor 
@Builder
@Table(uniqueConstraints = @UniqueConstraint(columnNames = { "userType", "userId" }))
public class Wallet {
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
  @Enumerated(EnumType.STRING) private UserType userType;
  private Long userId;
  private BigDecimal saldo;
}
