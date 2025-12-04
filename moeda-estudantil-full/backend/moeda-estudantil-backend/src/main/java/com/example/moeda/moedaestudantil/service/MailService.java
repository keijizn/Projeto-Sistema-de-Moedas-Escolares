package com.example.moeda.moedaestudantil.service;

import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class MailService {

    private final JavaMailSender mailSender;

    // remetente padrão (se não setar app.mail.from, usa o spring.mail.username)
    @Value("${app.mail.from:${spring.mail.username}}")
    private String from;

    public MailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendHtml(String to, String subject, String html) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, "UTF-8");

            helper.setTo(to);
            helper.setFrom(from);
            helper.setSubject(subject);
            helper.setText(html, true); // true = conteúdo em HTML

            mailSender.send(message);

            System.out.println("========================================");
            System.out.println("[MailService] E-MAIL ENVIADO DE VERDADE");
            System.out.println("Para: " + to);
            System.out.println("Assunto: " + subject);
            System.out.println("========================================");
        } catch (Exception e) {
            System.out.println("========================================");
            System.err.println("[MailService] ERRO AO ENVIAR E-MAIL");
            e.printStackTrace();
            System.out.println("========================================");
        }
    }
}
