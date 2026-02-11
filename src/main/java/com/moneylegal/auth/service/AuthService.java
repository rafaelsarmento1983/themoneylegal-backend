// backend/src/main/java/com/moneylegal/auth/service/AuthService.java
package com.moneylegal.auth.service;

import com.moneylegal.auth.dto.*;

/**
 * Interface AuthService - Serviço de Autenticação
 *
 * Responsabilidades:
 * - Registro de novos usuários
 * - Login (email/senha)
 * - Login OAuth (Google, Facebook, Apple)
 * - Refresh token
 * - Logout
 * - Recuperação de senha
 * - Verificação de email
 */
public interface AuthService {

    /**
     * Registrar novo usuário
     */
    AuthResponse register(RegisterRequest request);

    /**
     * Login com email e senha
     */
    AuthResponse login(LoginRequest request);

    /**
     * Login com Google OAuth
     */
    AuthResponse loginWithGoogle(String googleToken);

    /**
     * Login com Facebook OAuth
     */
    AuthResponse loginWithFacebook(String facebookToken);

    /**
     * Login com Apple OAuth
     */
    AuthResponse loginWithApple(String appleToken);

    /**
     * Refresh access token
     */
    AuthResponse refreshToken(RefreshTokenRequest request);

    /**
     * Logout (revogar refresh token)
     */
    void logout(String refreshToken);

    /**
     * Logout de todos os dispositivos (revogar todos os tokens)
     */
    void logoutAll(String userId);

    /**
     * Inicia processo de recuperação de senha com código OTP
     */
    ForgotPasswordResponse forgotPassword(ForgotPasswordRequest request);

    /**
     * Verifica se o código OTP é válido
     */
    VerifyResetCodeResponse verifyResetCode(VerifyResetCodeRequest request);

    /**
     * Redefine a senha usando código OTP
     */
    ResetPasswordResponse resetPassword(ResetPasswordRequest request);

    /**
     * Verificar email com token
     */
    void verifyEmail(VerifyEmailRequest request);

    /**
     * Reenviar email de verificação
     */
    void resendVerificationEmail(String email);

    PreRegisterResponse preRegister(PreRegisterRequest request);
    boolean existsByEmailWithPassword(String email);
}