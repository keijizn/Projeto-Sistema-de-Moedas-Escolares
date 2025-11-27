package com.example.moeda.moedaestudantil.service;
import com.example.moeda.moedaestudantil.domain.*; import com.example.moeda.moedaestudantil.repo.*;
import org.springframework.stereotype.Service; import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
@Service
public class WalletService {
  private final WalletRepository walletRepo;
  private final LedgerRepository ledgerRepo;
  public WalletService(WalletRepository walletRepo, LedgerRepository ledgerRepo) { this.walletRepo = walletRepo; this.ledgerRepo = ledgerRepo; }
  public Wallet getOrCreate(UserType type, Long userId) {
    return walletRepo.findByUserTypeAndUserId(type, userId).orElseGet(() -> walletRepo.save(Wallet.builder().userType(type).userId(userId).saldo(BigDecimal.ZERO).build()));
  }
  @Transactional
  public void transfer(UserType fromType, Long fromId, UserType toType, Long toId, int amount, String reason, LedgerKind kind) {
    if (amount <= 0) throw new IllegalArgumentException("Valor inválido");
    var from = getOrCreate(fromType, fromId);
    if (from.getSaldo().compareTo(BigDecimal.valueOf(amount)) < 0) throw new IllegalStateException("Saldo insuficiente");
    from.setSaldo(from.getSaldo().subtract(BigDecimal.valueOf(amount))); walletRepo.save(from);
    var to = getOrCreate(toType, toId);
    to.setSaldo(to.getSaldo().add(BigDecimal.valueOf(amount))); walletRepo.save(to);
    ledgerRepo.save(LedgerEntry.builder().kind(kind).fromType(fromType).fromId(fromId).toType(toType).toId(toId).amount(amount).reason(reason).build());
  }
}
