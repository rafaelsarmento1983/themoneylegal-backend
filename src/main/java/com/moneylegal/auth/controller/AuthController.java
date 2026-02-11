// backend/src/main/java/com/moneylegal/auth/controller/AuthController.java
package com.moneylegal.auth.controller;

import com.moneylegal.auth.dto.*;
import com.moneylegal.auth.service.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * AuthController - Endpoints de Autenticação
 *
 * Endpoints:
 * POST /api/v1/auth/register - Cadastro
 * POST /api/v1/auth/login - Login
 * POST /api/v1/auth/logout - Logout
 * POST /api/v1/auth/refresh - Refresh token
 * POST /api/v1/auth/forgot-password - Esqueci senha
 * POST /api/v1/auth/verify-reset-code - Verificar código OTP
 * POST /api/v1/auth/reset-password - Resetar senha
 * POST /api/v1/auth/verify-email - Verificar email
 * POST /api/v1/auth/resend-verification - Reenviar verificação
 * GET  /api/v1/auth/check-email - Verificar se email existe
 */
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final AuthService authService;
    private final UserService userService;

    /**
     * POST /api/v1/auth/register
     * Cadastrar novo usuário
     */
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        log.info("POST /api/v1/auth/register - email: {}", request.getEmail());
        AuthResponse response = authService.register(request);
        return ResponseEntity.ok(response);
    }

    /**
     * POST /api/v1/auth/login
     * Login com email e senha
     */
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        log.info("POST /api/v1/auth/login - email: {}", request.getEmail());
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    /**
     * POST /api/v1/auth/refresh
     * Renovar access token
     */
    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refreshToken(@Valid @RequestBody RefreshTokenRequest request) {
        log.info("POST /api/v1/auth/refresh");
        AuthResponse response = authService.refreshToken(request);
        return ResponseEntity.ok(response);
    }

    /**
     * POST /api/v1/auth/logout
     * Fazer logout (revogar refresh token)
     */
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@Valid @RequestBody RefreshTokenRequest request) {
        log.info("POST /api/v1/auth/logout");
        authService.logout(request.getRefreshToken());
        return ResponseEntity.noContent().build();
    }

    /**
     * POST /api/v1/auth/forgot-password
     * Solicitar recuperação de senha - Envia código OTP por email
     */
    @PostMapping("/forgot-password")
    public ResponseEntity<ForgotPasswordResponse> forgotPassword(
            @Valid @RequestBody ForgotPasswordRequest request) {
        log.info("POST /api/v1/auth/forgot-password - email: {}", request.getEmail());

        try {
            ForgotPasswordResponse response = authService.forgotPassword(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            // Por segurança, não revela se o email existe ou não
            log.warn("Forgot password error for email: {}", request.getEmail(), e);
            return ResponseEntity.ok(ForgotPasswordResponse.builder()
                    .message("Se o email existir, você receberá um código de recuperação")
                    .email(request.getEmail())
                    .build());
        }
    }

    /**
     * POST /api/v1/auth/verify-reset-code
     * Verificar se o código OTP é válido
     */
    @PostMapping("/verify-reset-code")
    public ResponseEntity<VerifyResetCodeResponse> verifyResetCode(
            @Valid @RequestBody VerifyResetCodeRequest request) {
        log.info("POST /api/v1/auth/verify-reset-code - email: {}", request.getEmail());
        VerifyResetCodeResponse response = authService.verifyResetCode(request);
        return ResponseEntity.ok(response);
    }

    /**
     * POST /api/v1/auth/reset-password
     * Resetar senha com código OTP
     */
    @PostMapping("/reset-password")
    public ResponseEntity<ResetPasswordResponse> resetPassword(
            @Valid @RequestBody ResetPasswordRequest request) {
        log.info("POST /api/v1/auth/reset-password - email: {}", request.getEmail());
        ResetPasswordResponse response = authService.resetPassword(request);
        return ResponseEntity.ok(response);
    }

    /**
     * POST /api/v1/auth/verify-email
     * Verificar email com token
     */
    @PostMapping("/verify-email")
    public ResponseEntity<Void> verifyEmail(@Valid @RequestBody VerifyEmailRequest request) {
        log.info("POST /api/v1/auth/verify-email");
        authService.verifyEmail(request);
        return ResponseEntity.ok().build();
    }

    /**
     * POST /api/v1/auth/resend-verification
     * Reenviar email de verificação
     */
    @PostMapping("/resend-verification")
    public ResponseEntity<Void> resendVerification(@RequestParam String email) {
        log.info("POST /api/v1/auth/resend-verification - email: {}", email);
        authService.resendVerificationEmail(email);
        return ResponseEntity.ok().build();
    }

    /**
     * GET /api/v1/auth/check-email
     * Verificar se email já está cadastrado (para validação em tempo real no frontend)
     *
     * @param email Email a ser verificado
     * @return EmailCheckResponse com exists (boolean) e message (string)
     */
    /*@GetMapping("/check-email")
    public ResponseEntity<EmailCheckResponse> checkEmail(@RequestParam String email) {
        log.info("GET /api/v1/auth/check-email - email: {}", email);

        // Validar formato do email
        if (email == null || email.trim().isEmpty()) {
            EmailCheckResponse response = EmailCheckResponse.builder()
                    .exists(false)
                    .message("Email inválido")
                    .build();
            return ResponseEntity.badRequest().body(response);
        }

        // Normalizar email (lowercase)
        String normalizedEmail = email.trim().toLowerCase();

        // Verificar se existe no banco (case insensitive)
        boolean exists = userService.existsByEmail(normalizedEmail);

        // Construir resposta
        EmailCheckResponse response = EmailCheckResponse.builder()
                .exists(exists)
                .message(exists
                        ? "Identificamos que este e-mail já possui cadastro."
                        : "Este e-mail pode ser utilizado para cadastro.")
                .build();

        log.debug("Email check result - email: {}, exists: {}", normalizedEmail, exists);

        return ResponseEntity.ok(response);
    }

     */

    /**
     * GET /api/v1/auth/check-email
     * Verificar se email já está cadastrado COM SENHA (cadastro completo)
     */
    @GetMapping("/check-email")
    public ResponseEntity<EmailCheckResponse> checkEmail(@RequestParam String email) {
        log.info("GET /api/v1/auth/check-email - email: {}", email);

        // Validar formato do email
        if (email == null || email.trim().isEmpty()) {
            EmailCheckResponse response = EmailCheckResponse.builder()
                    .exists(false)
                    .message("Email inválido")
                    .build();
            return ResponseEntity.badRequest().body(response);
        }

        // Normalizar email (lowercase)
        String normalizedEmail = email.trim().toLowerCase();

        // ⭐ Verificar se existe com SENHA definida (cadastro completo)
        boolean exists = authService.existsByEmailWithPassword(normalizedEmail);

        // Construir resposta
        EmailCheckResponse response = EmailCheckResponse.builder()
                .exists(exists)
                .message(exists
                        ? "Identificamos que este e-mail já possui cadastro."
                        : "Este e-mail pode ser utilizado para cadastro.")
                .build();

        log.debug("Email check result - email: {}, exists: {}", normalizedEmail, exists);

        return ResponseEntity.ok(response);
    }

    /**
     * POST /api/v1/auth/pre-register
     * PASSO 2: Criar usuário e enviar OTP (INSERT)
     */
    @PostMapping("/pre-register")
    public ResponseEntity<PreRegisterResponse> preRegister(
            @Valid @RequestBody PreRegisterRequest request) {
        log.info("POST /api/v1/auth/pre-register - email: {}", request.getEmail());
        PreRegisterResponse response = authService.preRegister(request);
        return ResponseEntity.ok(response);
    }

    // com/moneylegal/auth/controller/AuthController.java


}