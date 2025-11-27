package com.example.moeda.moedaestudantil.domain;
import jakarta.persistence.*; 
import lombok.*; 
import java.time.Instant;
@Entity 
@Getter 
@Setter 
@NoArgsConstructor 
@AllArgsConstructor 
@Builder
public class LedgerEntry {
  @Id 
  @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
  private Instant ts = Instant.now();
  @Enumerated(EnumType.STRING) private LedgerKind kind;
  @Enumerated(EnumType.STRING) private UserType fromType;
  private Long fromId;
  @Enumerated(EnumType.STRING) private UserType toType;
  private Long toId;
  private Integer amount;
  private String reason;
}
