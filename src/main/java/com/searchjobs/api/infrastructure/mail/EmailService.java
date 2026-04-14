package com.searchjobs.api.infrastructure.mail;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.internet.MimeMessage;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String from;

    @Value("${app.frontend-url}")
    private String frontendUrl;

    public void sendPasswordResetEmail(String toEmail, String token) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(from);
            helper.setTo(toEmail);
            helper.setSubject("SearchJobs — Recuperação de senha");

            String resetLink = frontendUrl + "/reset-password?token=" + token;

            String html = """
                    <div style="font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto;">
                        <h2 style="color: #1e293b;">Recuperação de senha</h2>
                        <p style="color: #475569;">
                            Recebemos uma solicitação para redefinir a senha da sua conta no SearchJobs.
                        </p>
                        <p style="color: #475569;">
                            Clique no botão abaixo para criar uma nova senha. 
                            Este link expira em <strong>30 minutos</strong>.
                        </p>
                        <a href="%s"
                           style="display: inline-block; background-color: #2563eb; color: white;
                                  padding: 12px 24px; border-radius: 8px; text-decoration: none;
                                  font-weight: bold; margin: 16px 0;">
                            Redefinir senha
                        </a>
                        <p style="color: #94a3b8; font-size: 13px; margin-top: 24px;">
                            Se você não solicitou a recuperação de senha, ignore este e-mail.
                            Sua senha permanece a mesma.
                        </p>
                        <hr style="border: none; border-top: 1px solid #e2e8f0; margin: 24px 0;">
                        <p style="color: #94a3b8; font-size: 12px;">SearchJobs — Sua plataforma de busca inteligente de vagas</p>
                    </div>
                    """.formatted(resetLink);

            helper.setText(html, true);
            mailSender.send(message);

        } catch (Exception e) {
            throw new RuntimeException("Erro ao enviar e-mail: " + e.getMessage());
        }
    }
}