package com.example.moeda.moedaestudantil.repo;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.moeda.moedaestudantil.domain.Wallet;
import com.example.moeda.moedaestudantil.domain.UserType;
import java.util.Optional;
public interface WalletRepository extends JpaRepository<Wallet, Long> {
  Optional<Wallet> findByUserTypeAndUserId(UserType userType, Long userId);
}
