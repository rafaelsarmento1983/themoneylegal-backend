package com.moneylegal.auth.service;

import com.moneylegal.config.SendGridConfig;
import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.SocketException;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final SendGrid sendGrid;
    private final SendGridConfig sendGridConfig;

    @Value("${SERVER_DEV_FRONT}")
    private String serverDevFront;

    private static final int MAX_RETRIES = 3;
    private static final long INITIAL_BACKOFF_MS = 1000; // 1 segundo

    /**
     * Método genérico para enviar emails usando Twilio SendGrid com retry
     */
    private void sendEmail(String to, String subject, String htmlContent) {
        int attempts = 0;
        long backoffMs = INITIAL_BACKOFF_MS;

        while (attempts < MAX_RETRIES) {
            try {
                attempts++;
                log.debug("Tentativa {} de {} para enviar email para: {}", attempts, MAX_RETRIES, to);

                Email from = new Email(sendGridConfig.getFromEmail(), sendGridConfig.getFromName());
                Email toEmail = new Email(to);
                Content content = new Content("text/html", htmlContent);

                Mail mail = new Mail(from, subject, toEmail, content);

                Request request = new Request();
                request.setMethod(Method.POST);
                request.setEndpoint("mail/send");
                request.setBody(mail.build());

                Response response = sendGrid.api(request);

                if (response.getStatusCode() >= 200 && response.getStatusCode() < 300) {
                    log.info("✅ Email enviado com sucesso para: {} - Status: {}", to, response.getStatusCode());
                    return; // Sucesso! Sair do método

                } else {
                    log.error("❌ Falha ao enviar email para: {} - Status: {} - Body: {}",
                            to, response.getStatusCode(), response.getBody());
                    throw new RuntimeException("Falha ao enviar email - Status: " + response.getStatusCode());
                }

            } catch (SocketException e) {
                log.warn("⚠️ Connection reset na tentativa {} de {} para: {}", attempts, MAX_RETRIES, to);

                if (attempts >= MAX_RETRIES) {
                    log.error("❌ Todas as {} tentativas falharam com Connection Reset", MAX_RETRIES);
                    throw new RuntimeException("Erro de conexão ao enviar email após " + MAX_RETRIES + " tentativas");
                }

                // Aguardar antes de tentar novamente (backoff exponencial)
                try {
                    log.info("⏳ Aguardando {}ms antes da próxima tentativa...", backoffMs);
                    Thread.sleep(backoffMs);
                    backoffMs *= 2; // Dobrar o tempo de espera
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException("Thread interrompida durante retry de email");
                }

            } catch (IOException e) {
                log.error("❌ Erro de IO ao enviar email para: {}", to, e);

                if (attempts >= MAX_RETRIES) {
                    throw new RuntimeException("Erro ao enviar email: " + e.getMessage());
                }

                // Aguardar antes de tentar novamente
                try {
                    Thread.sleep(backoffMs);
                    backoffMs *= 2;
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException("Thread interrompida durante retry de email");
                }
            }
        }

        // Se chegou aqui, todas as tentativas falharam
        throw new RuntimeException("Falha ao enviar email após " + MAX_RETRIES + " tentativas");
    }

    /**
     * Envia código OTP para recuperação de senha
     */
    public void sendPasswordResetCode(String to, String code, String userName) {
        try {
            String subject = "\uD83D\uDD10 Chegou seu código de acesso ao Money Legal!";
            String htmlContent = buildPasswordResetEmailHtml(code, userName, to);
            sendEmail(to, subject, htmlContent);
            log.info("Password reset email sent to: {}", to);
        } catch (Exception e) {
            log.error("Error sending password reset email to: {}", to, e);
            throw new RuntimeException("Erro ao enviar email de recuperação de senha");
        }
    }

    /**
     * Envia código OTP para VERIFICAÇÃO DE EMAIL no cadastro
     */
    public void sendEmailVerificationOtp(String to, String code, String userName) {
        try {
            String subject = "\uD83D\uDD10 Chegou seu código de acesso ao Money Legal!";
            String htmlContent = buildPasswordResetEmailHtml(code, userName, to);
            sendEmail(to, subject, htmlContent);
            log.info("Password reset email sent to: {}", to);
        } catch (Exception e) {
            log.error("Error sending password reset email to: {}", to, e);
            throw new RuntimeException("Erro ao enviar email de recuperação de senha");
        }
    }

    /**
     * Envia email de verificação de conta
     */
    public void sendVerificationEmail(String to, String verificationToken, String userName) {
        try {
            String subject = "Money Legal - Verifique seu Email";
            String htmlContent = buildVerificationEmailHtml(verificationToken, userName);
            sendEmail(to, subject, htmlContent);
            log.info("Verification email sent to: {}", to);
        } catch (Exception e) {
            log.error("Error sending verification email to: {}", to, e);
            throw new RuntimeException("Erro ao enviar email de verificação");
        }
    }

    /**
     * Template HTML para email de recuperação de senha
     */
    private String buildPasswordResetEmailHtml(String code, String userName, String to) {
        String template = """
                    <!DOCTYPE html>
                    <html>
                              
                    <head>
                        <meta charset="utf-8" />
                        <meta name="viewport" content="width=device-width,initial-scale=1" />
                    </head>
                                
                    <body style="
                                margin: 0;
                                background: #ffffff;
                                color: #0A0E16;
                                line-height: 1.5;
                                font-family: -apple-system, Montserrat, Segoe UI, Roboto, Helvetica,
                                    Arial, sans-serif;
                            ">
                        <div style="max-width: 700px; margin: 0 auto; padding: 24px">
                                <div
                                    style="
                                        background: #f9f9f9;
                                        border: 1px solid #e5e7eb;
                                        border-radius: 16px;
                                        padding: 24px;
                                    "
                                >
                                <h2 style="
                        margin: 0 0 10px 0;
                        font-weight: 800;
                        font-size: 22px;
                        color: #0A0E16;
                        text-align: center;
                      "> Código para seu acesso! </h2>
                    <p style="
                        margin: 0 0 18px 0;
                        font-size: 16px;
                        color: #6b7280;
                        text-align: center;
                      ">🔐 Utilize este código para verificar seu acesso. </p>
                                    <h3
                                        style="
                                            margin: 0 0 12px 0;
                                            font-weight: 700;
                                            font-size: 20px;
                                            color: #0A0E16;
                                        "
                                    >
                                        Olá, %s!
                                    </h3>
                                    <p style="margin: 0 0 12px 0; font-size: 16px">
                                        Recebemos uma solicitação de acesso para seu usuário. Para validar, por favor, forneça o código:
                                    </p>
                    <h1
                                        style="
                                            margin: 0 0 12px 0;
                                            font-size: 32px;
                                            text-align: center;
                    			color: #3B71FB;
                                        "
                                    >
                                         %s
                                    </h1>
                    <p style="
                                            margin: 0 0 12px 0;
                                            font-size: 12px;
                                            text-align: center;
                    color: #cccccc;
                                        "
                                    >
                    *Este código expira em 15 minutos.
                    </p>
                                
                                    <h3
                                        style="
                                            margin: 16px 0 8px 0;
                                            font-weight: 700;
                                            font-size: 18px;
                                            color: #0A0E16;
                                        "
                                    >
                                        Proteja sua conta!
                                    </h3>
                                    <p style="margin: 0 0 12px 0; font-size: 16px">
                                        Se você não solicitou este código ou não sabe quem fez a solicitação, recomendamos que faça logout no <b>Money Legal</b>. Caso queira também pode alterar sua senha.
                                    </p>
                                    <h3
                                        style="
                                            margin: 16px 0 8px 0;
                                            font-weight: 700;
                                            font-size: 18px;
                                            color: #0A0E16;
                                        "
                                    >
                                        Precisa de ajuda?
                                    </h3>
                                                    <p style="margin: 0 0 12px 0; font-size: 16px">
                                        Acesse a <font style="
                                            font-size: 16px;
                                            color: #3B71FB;
                                        ">
                    Central de Suporte ao Cliente</font> e fale com nosso time de especialistas.
                                    </p>
                    <h4
                                        style="
                                            margin: 16px 0 0 0;
                                            font-weight: 700;
                                            font-size: 16px;
                                            color: #0A0E16;
                                        "
                                    >
                                        Equipe Money Legal.
                                    </h4>
                                </div>
                                <div
                                    style="
                                        color: #6b7280;
                                        font-size: 12px;
                                        text-align: center;
                                        margin-top: 18px;
                                    "
                                >
                                    Money Legal - All rights reserved
                                </div>
                                <div
                                    style="
                                        color: #6b7280;
                                        font-size: 12px;
                                        text-align: center;
                                        margin-top: 18px;
                                    "
                                >
                                    <p style="margin: 0 0 8px 0">
                                        <a
                                            href="https://www.moneylegal.app/data-governance/privacy-policy"
                                            style="color: #3B71FB; text-decoration: underline"
                                            >Privacidade</a
                                        >
                                        •
                                        <a
                                            href="https://www.moneylegal.app/data-governance/ethics-n-conduct-policy"
                                            style="color: #3B71FB; text-decoration: underline"
                                            >Ética e Conduta</a
                                        >
                                        •
                                        <a
                                            href="https://www.moneylegal.app/data-governance/cookies-policy"
                                            style="color: #3B71FB; text-decoration: underline"
                                            >Uso de Cookies</a
                                        >
                                        •
                                        <a
                                            href="https://www.moneylegal.app/data-governance/terms-of-services-policy"
                                            style="color: #3B71FB; text-decoration: underline"
                                            >Termo de Uso e Serviços</a
                                        >
                                        •
                                        <a
                                            href="https://www.moneylegal.app/contact-us"
                                            style="color: #3B71FB; text-decoration: underline"
                                            >Fale Conosco</a
                                        >
                                        •
                                        <a
                                            href="https://dric-team.atlassian.net/servicedesk/customer/portals"
                                            style="color: #3B71FB; text-decoration: underline"
                                            >Central de Ajuda</a
                                        >
                                        •
                                        <a
                                            href="https://status-dric.statuspage.io/"
                                            style="color: #3B71FB; text-decoration: underline"
                                            >Status do Serviço</a
                                        >
                                    </p>
                                    <p style="margin: 8px 0 0 0; color: #cccccc; font-size: 12px">
                                        Esta mensagem foi enviada para
                                        <a
                                            href="https://dric-team.atlassian.net/servicedesk/customer/portals"
                                            style="color: #3B71FB; text-decoration: underline"
                                            >%s</a
                                        >
                                        porque foi utilizado um formlário eletrônico em
                                        <a
                                            href="https://www.moneylegal.app"
                                            style="color: #3B71FB; text-decoration: underline"
                                            >https://www.moneylegal.app</a
                                        >.
                                    </p>
                                </div>
                            </div>
                        </body>
                    </html>
                """;

        return String.format(template, userName, code, to);
    }

    /**
     * Template HTML para email de verificação de conta
     */
    private String buildVerificationEmailHtml(String token, String userName) {
        String verificationUrl = "http://" + serverDevFront + "/verify-email?token=" + token;

        String template = """
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <style>
                    body {
                        font-family: 'Arial', sans-serif;
                        line-height: 1.6;
                        color: #333;
                        background-color: #f4f4f4;
                        margin: 0;
                        padding: 0;
                    }
                    .container {
                        max-width: 600px;
                        margin: 20px auto;
                        background: #ffffff;
                        border-radius: 8px;
                        box-shadow: 0 2px 4px rgba(0,0,0,0.1);
                        overflow: hidden;
                    }
                    .header {
                        background: linear-gradient(135deg, #667eea 0%%, #764ba2 100%%);
                        color: white;
                        padding: 30px;
                        text-align: center;
                    }
                    .header h1 {
                        margin: 0;
                        font-size: 24px;
                    }
                    .content {
                        padding: 40px 30px;
                    }
                    .welcome {
                        background: #e7f3ff;
                        border-left: 4px solid #667eea;
                        padding: 15px;
                        margin: 20px 0;
                        border-radius: 4px;
                    }
                    .button {
                        display: inline-block;
                        background: linear-gradient(135deg, #667eea 0%%, #764ba2 100%%);
                        color: white;
                        padding: 15px 40px;
                        text-decoration: none;
                        border-radius: 5px;
                        font-weight: bold;
                        margin: 20px 0;
                        text-align: center;
                    }
                    .button:hover {
                        opacity: 0.9;
                    }
                    .link-box {
                        background: #f8f9fa;
                        border: 1px solid #dee2e6;
                        border-radius: 4px;
                        padding: 15px;
                        margin: 20px 0;
                        word-break: break-all;
                        font-size: 12px;
                        color: #6c757d;
                    }
                    .info {
                        background: #fff3cd;
                        border-left: 4px solid #ffc107;
                        padding: 15px;
                        margin: 20px 0;
                        border-radius: 4px;
                    }
                    .footer {
                        background: #f8f9fa;
                        padding: 20px 30px;
                        text-align: center;
                        font-size: 12px;
                        color: #6c757d;
                    }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <h1>Bem-vindo ao Money Legal!</h1>
                    </div>
                    <div class="content">
                        <div class="welcome">
                            <strong>Olá, %s!</strong>
                            <p style="margin: 10px 0 0 0;">Estamos muito felizes em ter você conosco! 🚀</p>
                        </div>
                        
                        <p>Para completar seu cadastro e começar a usar o Money Legal, você precisa verificar seu endereço de email.</p>
                        
                        <p style="text-align: center;">
                            <a href="%s" class="button">
                                ✅ Verificar Meu Email
                            </a>
                        </p>
                        
                        <p>Ou copie e cole o link abaixo no seu navegador:</p>
                        <div class="link-box">
                            %s
                        </div>
                        
                        <p>Após verificar seu email, você poderá:</p>
                        <ul>
                            <li>✅ Gerenciar suas finanças pessoais</li>
                            <li>✅ Criar orçamentos inteligentes</li>
                            <li>✅ Acompanhar seus gastos em tempo real</li>
                            <li>✅ E muito mais!</li>
                        </ul>
                        
                        <div class="info">
                            <strong>⚠️ Importante:</strong> Se você não se cadastrou no Money Legal, ignore este email.
                        </div>
                    </div>
                    <div class="footer">
                        <p>© 2026 Money Legal - Todos os direitos reservados</p>
                        <p>Este é um email automático, por favor não responda.</p>
                    </div>
                </div>
            </body>
            </html>
            """;

        return String.format(template, userName, verificationUrl, verificationUrl);
    }

    /**
     * Envia email de BOAS-VINDAS após completar o cadastro
     */
    public void sendWelcomeEmail(String to, String userName) {
        try {
            String subject = "🎉 Bem-vindo ao Money Legal!";
            String htmlContent = buildWelcomeEmailHtml(userName, to);
            sendEmail(to, subject, htmlContent);
            log.info("Welcome email sent to: {}", to);
        } catch (Exception e) {
            log.error("Error sending welcome email to: {}", to, e);
            // Não lançar exceção - falha no email de boas-vindas não deve bloquear cadastro
            log.warn("Cadastro completado mas email de boas-vindas falhou para: {}", to);
        }
    }

    /**
     * Template HTML para email de BOAS-VINDAS
     */
    private String buildWelcomeEmailHtml(String userName, String userEmail) {
        String template = """
                        <!DOCTYPE html>
                                                      <html>
                                                      <head>
                                                          <meta charset="UTF-8" />
                                                          <meta name="viewport" content="width=device-width,initial-scale=1" />
                                                      </head>
                                                      <body style="
                                                        margin: 0;
                                                        background: #ffffff;
                                                        color: #0A0E16;
                                                        line-height: 1.5;
                                                        font-family: -apple-system, Montserrat, Segoe UI, Roboto, Helvetica, Arial, sans-serif;
                                                      ">
                                                          <div style="max-width: 700px; margin: 0 auto; padding: 24px;">
                                                              <!-- Logo (opcional)
                                                              <div style="text-align: center; margin-bottom: 16px;">
                                                                <img src="https://localhost:3000/assets/logo.png" style="width: 220px; height: auto; border: 0; display: inline-block;" />
                                                              </div>
                                                              -->
                                                              <div style="
                                                                background: #f9f9f9;
                                                                border: 1px solid #e5e7eb;
                                                                border-radius: 16px;
                                                                padding: 24px;
                                                              ">
                                                                  <h2 style="
                                                                      margin: 0 0 10px 0;
                                                                      font-weight: 800;
                                                                      font-size: 22px;
                                                                      color: #0A0E16;
                                                                      text-align: center;
                                                                    ">Bem-vindo ao Money Legal! </h2>
                                                                  <p style="
                                                                      margin: 0 0 18px 0;
                                                                      font-size: 16px;
                                                                      color: #6b7280;
                                                                      text-align: center;
                                                                    "> 🎉 A rede social que cuida da sua jornada financeira.</p>
                                                                  <h3 style="
                                                                      margin: 0 0 12px 0;
                                                                      font-weight: 700;
                                                                      font-size: 20px;
                                                                      color: #0A0E16;
                                                                    "> Olá, {{USER_NAME}}! </h3>
                                                                  <p style="margin: 0 0 12px 0; font-size: 16px;"> Seu cadastro foi concluído com sucesso e você já pode começar a usar todas as funcionalidades do Money Legal. </p>
                                                                  <h3 style="
                                                                      margin: 20px 0 10px 0;
                                                                      font-weight: 700;
                                                                      font-size: 18px;
                                                                      color: #0A0E16;
                                                                    "> O que você pode fazer agora: </h3>
                                                                  <!-- Feature grid (tabelas são mais compatíveis em clientes de e-mail) -->
                                                                  <table role="presentation" cellpadding="0" cellspacing="0" width="100%" style="border-collapse: separate; border-spacing: 0px; margin: 0px 0px 0px 0px;">
                                                                      <tr>
                                                                          <td width="50%" valign="top" style="background: #ffffff; border: 1px solid #e5e7eb; border-radius: 12px; padding: 16px; text-align: left;">
                                              <p style="margin: 6px 6px 12px 6px; font-size: 16px"><font style="
                                                                      font-size: 16px;
                                                                      color: #3B71FB; font-weight: 700;
                                                                  "> 💰 Gerenciar Finanças:</font> Decisões melhores com receitas e despesas sempre atualizadas.</p>
                                              
                                              <p style="margin: 6px 6px 12px 6px; font-size: 16px"><font style="
                                                                      font-size: 16px;
                                                                      color: #3B71FB; font-weight: 700;
                                                                  "> 📊 Game das Finanças: </font> Entre no jogo para obter cashback, benefícios e muito mais.</p>
                                              
                                              <p style="margin: 6px 6px 12px 6px; font-size: 16px"><font style="
                                                                      font-size: 16px;
                                                                      color: #3B71FB; font-weight: 700;
                                                                  "> 🎯 Definir Metas:</font> Planeje, acompanhe e conquiste seus objetivos financeiros.</p>
                                              
                                                                  <p style="margin: 6px 6px 12px 6px; font-size: 16px"><font style="
                                                                      font-size: 16px;
                                                                      color: #3B71FB; font-weight: 700;
                                                                  "> 📈 Coach Financeiro:</font> Nosso assistente pode te ajudar sempre que for preciso.</p>
                                              
                                                                          </td>
                                                                      </tr>
                                                                  </table>
                                              
                                              
                                              
                                                                  <div style="text-align: center; margin: 18px 0 14px 0;">
                                                                      <a href="http://{{FRONTEND_URL}}" style="
                                                                        display: inline-block;
                                                                        background: #3B71FB;
                                                                        color: #ffffff;
                                                                        padding: 12px 22px;
                                                                        text-decoration: none;
                                                                        border-radius: 10px;
                                                                        font-weight: 600;
                                                                        font-size: 16px;
                                                                      "> Começar Agora </a>
                                                                  </div>
                                                                  <div style="
                                                                      background: #fff3cd;
                                                                      border: 1px solid #e5e7eb;
                                                                      border-left: 4px solid #ffc107;
                                                                      padding: 14px 14px;
                                                                      border-radius: 0px;
                                                                      margin: 10px 0 18px 0;
                                                                    ">
                                                                      <div style="font-size: 14px;">
                                                                          <b style="color: #0A0E16;">💡 Dica:</b>
                                                                          <span style="color: #0A0E16;"> Comece adicionando suas primeiras transações e tenha uma visão clara das suas finanças.</span>
                                                                      </div>
                                                                  </div>
                                                                  <h3 style="
                                                                      margin: 16px 0 8px 0;
                                                                      font-weight: 700;
                                                                      font-size: 18px;
                                                                      color: #0A0E16;
                                                                  "> Precisa de ajuda? </h3>
                                                                  <p style="margin: 0 0 12px 0; font-size: 16px"> Acesse a <font style="
                                                                      font-size: 16px;
                                                                      color: #3B71FB;
                                                                  "> Central de Suporte ao Cliente</font> e fale com nosso time de especialistas. </p>
                                                                  <h4 style="
                                                                      margin: 16px 0 0 0;
                                                                      font-weight: 700;
                                                                      font-size: 16px;
                                                                      color: #0A0E16;
                                                                  "> Equipe Money Legal. </h4>
                                                              </div>
                                                              <div style="
                                                                  color: #6b7280;
                                                                  font-size: 12px;
                                                                  text-align: center;
                                                                  margin-top: 18px;
                                                              "> Money Legal - All rights reserved </div>
                                                              <div style="
                                                                  color: #6b7280;
                                                                  font-size: 12px;
                                                                  text-align: center;
                                                                  margin-top: 18px;
                                                              ">
                                                                  <p style="margin: 0 0 8px 0">
                                                                      <a href="https://www.moneylegal.app/data-governance/privacy-policy" style="color: #3B71FB; text-decoration: underline">Privacidade</a> • <a href="https://www.moneylegal.app/data-governance/ethics-n-conduct-policy" style="color: #3B71FB; text-decoration: underline">Ética e Conduta</a> • <a href="https://www.moneylegal.app/data-governance/cookies-policy" style="color: #3B71FB; text-decoration: underline">Uso de Cookies</a> • <a href="https://www.moneylegal.app/data-governance/terms-of-services-policy" style="color: #3B71FB; text-decoration: underline">Termo de Uso e Serviços</a> • <a href="https://www.moneylegal.app/contact-us" style="color: #3B71FB; text-decoration: underline">Fale Conosco</a> • <a href="https://dric-team.atlassian.net/servicedesk/customer/portals" style="color: #3B71FB; text-decoration: underline">Central de Ajuda</a> • <a href="https://status-dric.statuspage.io/" style="color: #3B71FB; text-decoration: underline">Status do Serviço</a>
                                                                  </p>
                                                                  <p style="margin: 8px 0 0 0; color: #cccccc; font-size: 12px"> Esta mensagem foi enviada para <a href="https://dric-team.atlassian.net/servicedesk/customer/portals" style="color: #3B71FB; text-decoration: underline">{{USER_EMAIL}}</a> porque foi utilizado um formlário eletrônico em <a href="https://www.moneylegal.app" style="color: #3B71FB; text-decoration: underline">https://www.moneylegal.app</a>. </p>
                                                              </div>
                                                      </body>
                                                      </html>
                """;

        String frontendUrl = serverDevFront != null ? serverDevFront : "localhost:3000";

        return template
                .replace("{{USER_NAME}}", userName)
                .replace("{{FRONTEND_URL}}", frontendUrl)
                .replace("{{USER_EMAIL}}", userEmail);
    }
}