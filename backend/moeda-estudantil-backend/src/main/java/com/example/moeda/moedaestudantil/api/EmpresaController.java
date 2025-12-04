package com.example.moeda.moedaestudantil.api;

import com.example.moeda.moedaestudantil.domain.LedgerKind;
import com.example.moeda.moedaestudantil.domain.UserType;
import com.example.moeda.moedaestudantil.dto.BenefitDtos.Create;
import com.example.moeda.moedaestudantil.dto.TransferDtos.GrantProfessor;
import com.example.moeda.moedaestudantil.service.BenefitService;
import com.example.moeda.moedaestudantil.service.EmpresaService;
import com.example.moeda.moedaestudantil.service.WalletService;
import com.example.moeda.moedaestudantil.repo.LedgerRepository;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/api/empresas")
@CrossOrigin
public class EmpresaController {

    private final EmpresaService svc;
    private final BenefitService benefits;
    private final WalletService wallet;
    private final LedgerRepository ledgerRepo;

    public EmpresaController(
            EmpresaService svc,
            BenefitService benefits,
            WalletService wallet,
            LedgerRepository ledgerRepo
    ) {
        this.svc = svc;
        this.benefits = benefits;
        this.wallet = wallet;
        this.ledgerRepo = ledgerRepo;
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> get(@PathVariable("id") Long id) {
        return ResponseEntity.ok(svc.get(id));
    }

    // POST antigo JSON (sem foto) – continua funcionando
    @PostMapping("/{id}/beneficios")
    public ResponseEntity<?> createBenefit(@PathVariable("id") Long id,
                                           @Valid @RequestBody Create dto) {
        Long benefitId = benefits.create(id, dto);
        return ResponseEntity.ok(Map.of("benefitId", benefitId));
    }

    // NOVO: POST multipart com foto
    @PostMapping("/{id}/beneficios/upload")
    public ResponseEntity<?> createBenefitWithImage(
            @PathVariable("id") Long id,
            @RequestParam("titulo") String titulo,
            @RequestParam(value = "descricao", required = false) String descricao,
            @RequestParam("custo") Integer custo,
            @RequestParam(value = "foto", required = false) MultipartFile foto
    ) throws IOException {

        byte[] bytes = null;
        String contentType = null;

        if (foto != null && !foto.isEmpty()) {
            bytes = foto.getBytes();
            contentType = foto.getContentType();
        }

        Long benefitId = benefits.createWithImage(id, titulo, descricao, custo, bytes, contentType);
        return ResponseEntity.ok(Map.of("benefitId", benefitId));
    }

    // Lista benefícios da empresa
    @GetMapping("/{id}/beneficios")
    public ResponseEntity<?> listBenefits(@PathVariable("id") Long id) {
        return ResponseEntity.ok(benefits.listByEmpresa(id));
    }

    @GetMapping("/{id}/wallet")
    public ResponseEntity<?> saldo(@PathVariable("id") Long id) {
        var w = wallet.getOrCreate(UserType.EMPRESA, id);
        return ResponseEntity.ok(Map.of("saldo", w.getSaldo()));
    }

    @GetMapping("/{id}/ledger")
    public ResponseEntity<?> ledger(@PathVariable("id") Long id) {
        return ResponseEntity.ok(
                ledgerRepo.findByFromTypeAndFromIdOrToTypeAndToIdOrderByTsDesc(
                        UserType.EMPRESA, id,
                        UserType.EMPRESA, id
                )
        );
    }

    @PostMapping("/{id}/grant")
    public ResponseEntity<?> grant(@PathVariable("id") Long id,
                                   @Valid @RequestBody GrantProfessor dto) {
        wallet.transfer(
                UserType.EMPRESA, id,
                UserType.PROFESSOR, dto.professorId,
                dto.amount,
                dto.reason,
                LedgerKind.GRANT
        );
        return ResponseEntity.ok().build();
    }
}
