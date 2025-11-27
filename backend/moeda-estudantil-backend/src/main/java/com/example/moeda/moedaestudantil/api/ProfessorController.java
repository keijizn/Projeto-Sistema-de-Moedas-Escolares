package com.example.moeda.moedaestudantil.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.util.Map;

import com.example.moeda.moedaestudantil.dto.TransferDtos.GrantAluno;
import com.example.moeda.moedaestudantil.domain.LedgerKind;
import com.example.moeda.moedaestudantil.domain.UserType;
import com.example.moeda.moedaestudantil.service.WalletService;
import com.example.moeda.moedaestudantil.service.ProfessorService;
import com.example.moeda.moedaestudantil.repo.LedgerRepository;

@RestController
@RequestMapping("/api/professores")
@CrossOrigin
public class ProfessorController {

    private final ProfessorService svc;
    private final WalletService wallet;
    private final LedgerRepository ledgerRepo;

    public ProfessorController(ProfessorService svc,
                               WalletService wallet,
                               LedgerRepository ledgerRepo) {
        this.svc = svc;
        this.wallet = wallet;
        this.ledgerRepo = ledgerRepo;
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> get(@PathVariable("id") Long id) {
        return ResponseEntity.ok(svc.get(id));
    }

    @GetMapping("/{id}/wallet")
    public ResponseEntity<?> saldo(@PathVariable("id") Long id) {
        var w = wallet.getOrCreate(UserType.PROFESSOR, id);
        return ResponseEntity.ok(Map.of("saldo", w.getSaldo()));
    }

    @GetMapping("/{id}/ledger")
    public ResponseEntity<?> ledger(@PathVariable("id") Long id) {
        return ResponseEntity.ok(
            ledgerRepo.findByFromTypeAndFromIdOrToTypeAndToIdOrderByTsDesc(
                UserType.PROFESSOR, id,
                UserType.PROFESSOR, id
            )
        );
    }

    @PostMapping("/{id}/grant")
    public ResponseEntity<?> grant(@PathVariable("id") Long id,
                                   @Valid @RequestBody GrantAluno dto) {
        wallet.transfer(
            UserType.PROFESSOR, id,
            UserType.ALUNO, dto.alunoId,
            dto.amount, dto.reason,
            LedgerKind.GRANT
        );
        return ResponseEntity.ok().build();
    }
}
