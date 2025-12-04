package com.example.moeda.moedaestudantil.repo;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.moeda.moedaestudantil.domain.Aluno;
import java.util.Optional;
public interface AlunoRepository extends JpaRepository<Aluno, Long> {
  Optional<Aluno> findByEmail(String email);
  boolean existsByEmail(String email);
  boolean existsByCpf(String cpf);
}
