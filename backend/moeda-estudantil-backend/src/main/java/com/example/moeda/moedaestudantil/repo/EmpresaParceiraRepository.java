package com.example.moeda.moedaestudantil.repo;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.moeda.moedaestudantil.domain.EmpresaParceira;
import java.util.Optional;
public interface EmpresaParceiraRepository extends JpaRepository<EmpresaParceira, Long> {
  Optional<EmpresaParceira> findByEmail(String email);
  boolean existsByEmail(String email);
  boolean existsByCnpj(String cnpj);
}
