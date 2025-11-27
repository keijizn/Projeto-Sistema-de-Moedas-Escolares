package com.example.moeda.moedaestudantil.config;
import org.springframework.boot.CommandLineRunner; 
import org.springframework.context.annotation.Bean; 
import org.springframework.context.annotation.Configuration;
import com.example.moeda.moedaestudantil.domain.*; 
import com.example.moeda.moedaestudantil.repo.*; 
import com.example.moeda.moedaestudantil.service.*;
import java.math.BigDecimal;
@Configuration
public class DataLoader {
  @Bean CommandLineRunner seed(AlunoRepository alunoRepo, ProfessorRepository profRepo, EmpresaParceiraRepository empRepo, WalletRepository walletRepo, BenefitRepository benRepo, PasswordService password) {
    return args -> {
      if (alunoRepo.count() == 0 && profRepo.count() == 0 && empRepo.count() == 0) {
        var a = alunoRepo.save(Aluno.builder().nome("Ana Aluna").curso("Engenharia").email("aluno@ex.com").cpf("000.000.000-00").senhaHash(password.hash("123")).build());
        var p = profRepo.save(Professor.builder().nome("Paulo Prof").cpf("111.111.111-11").email("prof@ex.com").senhaHash(password.hash("123")).build());
        var e = empRepo.save(EmpresaParceira.builder().cnpj("22.222.222/0001-22").nome("Livraria Campus").email("parceira@ex.com").senhaHash(password.hash("123")).build());
        walletRepo.save(Wallet.builder().userType(UserType.ALUNO).userId(a.getId()).saldo(new BigDecimal("200")).build());
        walletRepo.save(Wallet.builder().userType(UserType.PROFESSOR).userId(p.getId()).saldo(new BigDecimal("1000")).build());
        walletRepo.save(Wallet.builder().userType(UserType.EMPRESA).userId(e.getId()).saldo(new BigDecimal("0")).build());
        benRepo.save(Benefit.builder().empresaId(e.getId()).titulo("10% desconto cadernos").descricao("Válido na loja física").custo(50).ativo(true).build());
      }
    };
  }
}
