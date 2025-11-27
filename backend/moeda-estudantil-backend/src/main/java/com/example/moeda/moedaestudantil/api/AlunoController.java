package com.example.moeda.moedaestudantil.api;

import com.example.moeda.moedaestudantil.domain.BenefitRedemption;
import com.example.moeda.moedaestudantil.domain.LedgerKind;
import com.example.moeda.moedaestudantil.domain.UserType;
import com.example.moeda.moedaestudantil.repo.BenefitRedemptionRepository;
import com.example.moeda.moedaestudantil.repo.LedgerRepository;
import com.example.moeda.moedaestudantil.service.AlunoService;
import com.example.moeda.moedaestudantil.service.BenefitService;
import com.example.moeda.moedaestudantil.service.EmailService;
import com.example.moeda.moedaestudantil.service.WalletService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.SecureRandom;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Map;

@RestController
@RequestMapping("/api/alunos")
@CrossOrigin
public class AlunoController {

    private final AlunoService alunoService;
    private final WalletService walletService;
    private final LedgerRepository ledgerRepo;
    private final BenefitService benefitService;
    private final BenefitRedemptionRepository redemptionRepo;
    private final EmailService emailService; // <<< agora usamos EmailService (EmailJS)

    // gerador de código
    private final SecureRandom random = new SecureRandom();
    private static final String CODE_CHARS = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";

    public AlunoController(
            AlunoService alunoService,
            WalletService walletService,
            LedgerRepository ledgerRepo,
            BenefitService benefitService,
            BenefitRedemptionRepository redemptionRepo,
            EmailService emailService
    ) {
        this.alunoService = alunoService;
        this.walletService = walletService;
        this.ledgerRepo = ledgerRepo;
        this.benefitService = benefitService;
        this.redemptionRepo = redemptionRepo;
        this.emailService = emailService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> get(@PathVariable("id") Long id) {
        return ResponseEntity.ok(alunoService.get(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable("id") Long id,
                                    @RequestBody Map<String, String> body) {
        var a = alunoService.update(id, body.get("nome"), body.get("curso"), body.get("email"));
        return ResponseEntity.ok(a);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable("id") Long id) {
        alunoService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/wallet")
    public ResponseEntity<?> saldo(@PathVariable("id") Long id) {
        var w = walletService.getOrCreate(UserType.ALUNO, id);
        return ResponseEntity.ok(Map.of("saldo", w.getSaldo()));
    }

    @GetMapping("/{id}/ledger")
    public ResponseEntity<?> ledger(@PathVariable("id") Long id) {
        return ResponseEntity.ok(
                ledgerRepo.findByFromTypeAndFromIdOrToTypeAndToIdOrderByTsDesc(
                        UserType.ALUNO, id, UserType.ALUNO, id
                )
        );
    }

    @PostMapping("/{id}/redeem/{benefitId}")
    public ResponseEntity<?> redeem(@PathVariable("id") Long id,
                                    @PathVariable("benefitId") Long benefitId) {

        // 1) acha o benefício ativo
        var b = benefitService.listAllActive().stream()
                .filter(x -> x.getId().equals(benefitId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Benefício não encontrado"));

        // 2) transfere moedas do ALUNO -> EMPRESA
        walletService.transfer(
                UserType.ALUNO, id,
                UserType.EMPRESA, b.getEmpresaId(),
                b.getCusto(),
                "Resgate: " + b.getTitulo(),
                LedgerKind.REDEEM
        );

        // 3) gera código de resgate
        String code = generateCode();

        Instant now = Instant.now();
        Instant expiresAt = now.plus(1, ChronoUnit.DAYS); // expira em 24h

        var redemption = BenefitRedemption.builder()
                .alunoId(id)
                .empresaId(b.getEmpresaId())
                .benefitId(b.getId())
                .code(code)
                .createdAt(now)
                .expiresAt(expiresAt)
                .used(false)
                .build();

        redemptionRepo.save(redemption);

        // 4) Envia e-mail para o aluno via EmailJS
        try {
            var aluno = alunoService.get(id); // lança se não existir

            emailService.sendBenefitRedemptionEmail(
                    aluno.getEmail(),
                    aluno.getNome(),
                    b.getTitulo(),
                    b.getCusto(),
                    code
            );
        } catch (Exception e) {
            // Não quebra o fluxo do resgate se o e-mail falhar
            e.printStackTrace();
        }

        // 5) devolve o código para o frontend exibir para o aluno
        Map<String, Object> resp = Map.of(
                "code", code,
                "expiresAt", expiresAt.toString(),
                "benefitTitle", b.getTitulo()
        );

        return ResponseEntity.ok(resp);
    }

    // ====== helper ======
    private String generateCode() {
        StringBuilder sb = new StringBuilder(8); // 8 caracteres
        for (int i = 0; i < 8; i++) {
            int idx = random.nextInt(CODE_CHARS.length());
            sb.append(CODE_CHARS.charAt(idx));
        }
        return sb.toString();
    }
}
