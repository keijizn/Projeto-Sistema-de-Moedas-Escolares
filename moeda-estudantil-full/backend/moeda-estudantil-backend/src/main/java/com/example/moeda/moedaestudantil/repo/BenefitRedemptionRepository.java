package com.example.moeda.moedaestudantil.repo;

import com.example.moeda.moedaestudantil.domain.BenefitRedemption;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BenefitRedemptionRepository extends JpaRepository<BenefitRedemption, Long> {

    Optional<BenefitRedemption> findByCode(String code);
}
