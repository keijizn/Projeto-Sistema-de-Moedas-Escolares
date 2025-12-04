package com.example.moeda.moedaestudantil.repo;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.moeda.moedaestudantil.domain.Professor;
import java.util.Optional;
public interface ProfessorRepository extends JpaRepository<Professor, Long> {
  Optional<Professor> findByEmail(String email);
  boolean existsByEmail(String email);
  boolean existsByCpf(String cpf);
}
