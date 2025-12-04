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

    // template específico para redefinição de senha
    @Value("${emailjs.template-id-reset-password}")
    private String resetPasswordTemplateId;

    @Value("${emailjs.public-key}")
    private String publicKey;

    private final ObjectMapper mapper = new ObjectMapper();

    /**
     * Versão antiga (compatível) SEM imagem.
     * Mantida para não quebrar chamadas existentes.
     */
    public void sendBenefitRedemptionEmail(
            String toEmail,
            String alunoNome,
            String beneficioTitulo,
            int beneficioCusto,
            String codigoResgate
    ) {
        // delega para a versão nova com imagem = null
        sendBenefitRedemptionEmail(
                toEmail,
                alunoNome,
                beneficioTitulo,
                beneficioCusto,
                codigoResgate,
                null
        );
    }

    /**
     * Envia e-mail de resgate de benefício usando EmailJS.
     *
     * Espera que o template no EmailJS use as variáveis:
     *  - to_email
     *  - aluno_nome
     *  - beneficio_titulo
     *  - beneficio_custo
     *  - codigo_resgate
     *  - beneficio_imagem (URL da imagem do benefício)
     */
    public void sendBenefitRedemptionEmail(
            String toEmail,
            String alunoNome,
            String beneficioTitulo,
            int beneficioCusto,
            String codigoResgate,
            String beneficioImagemUrl
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

            // só envia a variável se tiver URL
            if (beneficioImagemUrl != null && !beneficioImagemUrl.isBlank()) {
                templateParams.put("beneficio_imagem", beneficioImagemUrl);
            }

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
                System.out.println("[EmailService] EmailJS OK (redeem): HTTP " + status);
            } else {
                System.err.println("[EmailService] EmailJS erro (redeem): HTTP " + status);
            }

            conn.disconnect();
        } catch (Exception e) {
            System.err.println("[EmailService] Falha ao chamar EmailJS (redeem)");
            e.printStackTrace();
        }
    }

    // ==========================================================
    // E-mail de redefinição de senha via EmailJS
    // ==========================================================
    /**
     * Template no EmailJS deve usar:
     *  - to_email
     *  - tipo_usuario
     *  - nova_senha
     */
    public void sendPasswordResetEmail(String toEmail, String tipoUsuario, String novaSenha) {
        System.out.println("[EmailService] Preparando envio de reset para " + toEmail +
                " | tipo_usuario=" + tipoUsuario +
                " | nova_senha=" + novaSenha);

        if (toEmail == null || toEmail.isBlank()) {
            System.err.println("[EmailService] E-mail vazio em reset de senha, não enviando.");
            return;
        }

        try {
            URL url = new URL("https://api.emailjs.com/api/v1.0/email/send");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            Map<String, Object> templateParams = new HashMap<>();
            templateParams.put("to_email", toEmail);
            templateParams.put("tipo_usuario", tipoUsuario);
            templateParams.put("nova_senha", novaSenha);

            Map<String, Object> payload = new HashMap<>();
            payload.put("service_id", serviceId);
            payload.put("template_id", resetPasswordTemplateId);
            payload.put("user_id", publicKey);
            payload.put("template_params", templateParams);

            String json = mapper.writeValueAsString(payload);

            try (var os = conn.getOutputStream()) {
                os.write(json.getBytes(StandardCharsets.UTF_8));
            }

            int status = conn.getResponseCode();
            if (status >= 200 && status < 300) {
                System.out.println("[EmailService] EmailJS OK (reset password): HTTP " + status);
            } else {
                System.err.println("[EmailService] EmailJS erro (reset password): HTTP " + status);
            }

            conn.disconnect();
        } catch (Exception e) {
            System.err.println("[EmailService] Falha ao chamar EmailJS (reset password)");
            e.printStackTrace();
        }
    }
}
