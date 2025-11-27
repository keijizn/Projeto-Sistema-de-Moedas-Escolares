package com.example.moeda.moedaestudantil.repo;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.moeda.moedaestudantil.domain.LedgerEntry;
import com.example.moeda.moedaestudantil.domain.UserType;
import java.util.List;
public interface LedgerRepository extends JpaRepository<LedgerEntry, Long> {
  List<LedgerEntry> findByFromTypeAndFromIdOrToTypeAndToIdOrderByTsDesc(UserType fromType, Long fromId, UserType toType, Long toId);
  List<LedgerEntry> findByFromTypeAndFromIdOrderByTsDesc(UserType type, Long id);
  List<LedgerEntry> findByToTypeAndToIdOrderByTsDesc(UserType type, Long id);
}
