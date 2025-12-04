package com.example.moeda.moedaestudantil.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Service
public class EmailService {

    @Value("${emailjs.service-id}")
    private String serviceId;

    // template específico para resgate de benefício
    @Value("${emailjs.template-id-redeem}")
    private String redeemTemplateId;

    @Value("${emailjs.public-key}")
    private String publicKey;

    private final ObjectMapper mapper = new ObjectMapper();

    /**
     * Envia e-mail de resgate de benefício usando EmailJS.
     *
     * Espera que o template no EmailJS use as variáveis:
     *  - to_email
     *  - aluno_nome
     *  - beneficio_titulo
     *  - beneficio_custo
     *  - codigo_resgate
     */
    public void sendBenefitRedemptionEmail(
            String toEmail,
            String alunoNome,
            String beneficioTitulo,
            int beneficioCusto,
            String codigoResgate
    ) {
        if (toEmail == null || toEmail.isBlank()) {
            System.err.println("[EmailService] E-mail do aluno vazio, não enviando.");
            return;
        }

        try {
            URL url = new URL("https://api.emailjs.com/api/v1.0/email/send");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            // Parâmetros que o template do EmailJS vai receber
            Map<String, Object> templateParams = new HashMap<>();
            templateParams.put("to_email", toEmail);
            templateParams.put("aluno_nome", alunoNome);
            templateParams.put("beneficio_titulo", beneficioTitulo);
            templateParams.put("beneficio_custo", beneficioCusto);
            templateParams.put("codigo_resgate", codigoResgate);

            // Corpo da requisição para o EmailJS
            Map<String, Object> payload = new HashMap<>();
            payload.put("service_id", serviceId);
            payload.put("template_id", redeemTemplateId);
            payload.put("user_id", publicKey);
            payload.put("template_params", templateParams);

            String json = mapper.writeValueAsString(payload);

            try (var os = conn.getOutputStream()) {
                os.write(json.getBytes(StandardCharsets.UTF_8));
            }

            int status = conn.getResponseCode();
            if (status >= 200 && status < 300) {
                System.out.println("[EmailService] EmailJS OK: HTTP " + status);
            } else {
                System.err.println("[EmailService] EmailJS erro: HTTP " + status);
            }

            conn.disconnect();
        } catch (Exception e) {
            System.err.println("[EmailService] Falha ao chamar EmailJS");
            e.printStackTrace();
        }
    }
}
