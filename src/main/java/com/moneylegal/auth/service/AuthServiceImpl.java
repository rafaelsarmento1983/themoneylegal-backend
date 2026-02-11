// backend/src/main/java/com/moneylegal/auth/service/AuthServiceImpl.java
package com.moneylegal.auth.service;

import com.moneylegal.auth.dto.*;
import com.moneylegal.auth.entity.RefreshToken;
import com.moneylegal.auth.entity.User;
import com.moneylegal.auth.repository.RefreshTokenRepository;
import com.moneylegal.auth.repository.UserRepository;
import com.moneylegal.exception.BadRequestException;
import com.moneylegal.exception.UnauthorizedException;
import com.moneylegal.security.JwtTokenProvider;
import com.moneylegal.tenant.entity.Tenant;
import com.moneylegal.tenant.entity.TenantMember;
import com.moneylegal.tenant.repository.TenantMemberRepository;
import com.moneylegal.tenant.repository.TenantRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;

/**
 * Implementação do AuthService
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final TenantRepository tenantRepository;
    private final TenantMemberRepository tenantMemberRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final EmailService emailService;

    private static final int REFRESH_TOKEN_EXPIRATION_DAYS = 7;
    private static final int ACCESS_TOKEN_EXPIRATION_MINUTES = 15;
    private static final int OTP_EXPIRATION_MINUTES = 15;
    private static final int OTP_LENGTH = 6;

    /*@Override
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        log.info("Registrando novo usuário: {}", request.getEmail());

        // Validar se email já existe
        if (userRepository.existsByEmailIgnoreCase(request.getEmail())) {
            throw new BadRequestException("Email já cadastrado");
        }

        // Validar telefone se fornecido
        if (request.getPhone() != null && userRepository.existsByPhone(request.getPhone())) {
            throw new BadRequestException("Telefone já cadastrado");
        }

        // Criar usuário
        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail().toLowerCase())
                .phone(request.getPhone())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .emailVerified(false)
                .phoneVerified(false)
                .isActive(true)
                .build();

        user = userRepository.save(user);
        log.info("Usuário criado com sucesso: {}", user.getId());

        // Criar tenant pessoal automático
        Tenant personalTenant = createPersonalTenant(user);

        // Criar membership (OWNER)
        TenantMember membership = TenantMember.builder()
                .tenantId(personalTenant.getId())
                .userId(user.getId())
                .role(TenantMember.MemberRole.OWNER)
                .isActive(true)
                .build();
        tenantMemberRepository.save(membership);

        // TODO: Enviar email de verificação
        emailService.sendVerificationEmail(user.getEmail(), verificationToken, user.getName());

        // Gerar tokens
        String accessToken = jwtTokenProvider.generateAccessToken(user.getId());
        String refreshToken = jwtTokenProvider.generateRefreshToken();

        // Salvar refresh token
        saveRefreshToken(user.getId(), refreshToken);

        // Construir response
        return buildAuthResponse(user, accessToken, refreshToken, personalTenant, TenantMember.MemberRole.OWNER);
    }
    */

    @Override
    @Transactional
    public AuthResponse login(LoginRequest request) {
        log.info("Login attempt: {}", request.getEmail());

        // Buscar usuário
        User user = userRepository.findByEmailIgnoreCase(request.getEmail())
                .orElseThrow(() -> new UnauthorizedException("Por favor, verifique seus dados de acesso e tente novamente."));

        // Verificar senha
        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new UnauthorizedException("Por favor, verifique seus dados de acesso e tente novamente.");
        }

        // Verificar se usuário está ativo
        if (!user.getIsActive()) {
            throw new UnauthorizedException("Este usuário pode estar inativo ou bloqueado. Tente novamente mais tarde.");
        }

        // Atualizar last login
        user.updateLastLogin();
        userRepository.save(user);

        // Buscar tenant padrão (primeiro ativo)
        TenantMember defaultMembership = tenantMemberRepository.findByUserIdAndIsActiveTrue(user.getId())
                .stream()
                .findFirst()
                .orElseThrow(() -> new BadRequestException("Usuário sem tenant"));

        Tenant defaultTenant = tenantRepository.findById(defaultMembership.getTenantId())
                .orElseThrow(() -> new BadRequestException("Tenant não encontrado"));

        // Gerar tokens
        String accessToken = jwtTokenProvider.generateAccessToken(user.getId());
        String refreshToken = jwtTokenProvider.generateRefreshToken();

        // Salvar refresh token
        saveRefreshToken(user.getId(), refreshToken);

        log.info("Login successful: {}", user.getId());

        return buildAuthResponse(user, accessToken, refreshToken, defaultTenant, defaultMembership.getRole());
    }

    @Override
    @Transactional
    public AuthResponse loginWithGoogle(String googleToken) {
        // TODO: Implementar OAuth Google
        // 1. Validar token com Google API
        // 2. Extrair dados (email, nome, foto)
        // 3. Criar ou buscar usuário
        // 4. Gerar tokens JWT
        throw new UnsupportedOperationException("Google OAuth não implementado ainda");
    }

    @Override
    @Transactional
    public AuthResponse loginWithFacebook(String facebookToken) {
        // TODO: Implementar OAuth Facebook
        throw new UnsupportedOperationException("Facebook OAuth não implementado ainda");
    }

    @Override
    @Transactional
    public AuthResponse loginWithApple(String appleToken) {
        // TODO: Implementar OAuth Apple
        throw new UnsupportedOperationException("Apple OAuth não implementado ainda");
    }

    @Override
    @Transactional
    public AuthResponse refreshToken(RefreshTokenRequest request) {
        log.info("Refresh token request");

        // Validar refresh token
        RefreshToken refreshToken = refreshTokenRepository.findValidToken(
                request.getRefreshToken(),
                LocalDateTime.now()
        ).orElseThrow(() -> new UnauthorizedException("Refresh token inválido ou expirado"));

        // Buscar usuário
        User user = userRepository.findById(refreshToken.getUserId())
                .orElseThrow(() -> new UnauthorizedException("Usuário não encontrado"));

        // Verificar se usuário está ativo
        if (!user.getIsActive()) {
            throw new UnauthorizedException("Usuário inativo");
        }

        // Revogar token antigo
        refreshToken.revoke();
        refreshTokenRepository.save(refreshToken);

        // Gerar novos tokens
        String newAccessToken = jwtTokenProvider.generateAccessToken(user.getId());
        String newRefreshToken = jwtTokenProvider.generateRefreshToken();

        // Salvar novo refresh token
        saveRefreshToken(user.getId(), newRefreshToken);

        // Buscar tenant padrão
        TenantMember defaultMembership = tenantMemberRepository.findByUserIdAndIsActiveTrue(user.getId())
                .stream()
                .findFirst()
                .orElseThrow(() -> new BadRequestException("Usuário sem tenant"));

        Tenant defaultTenant = tenantRepository.findById(defaultMembership.getTenantId())
                .orElseThrow(() -> new BadRequestException("Tenant não encontrado"));

        log.info("Refresh token successful: {}", user.getId());

        return buildAuthResponse(user, newAccessToken, newRefreshToken, defaultTenant, defaultMembership.getRole());
    }

    @Override
    @Transactional
    public void logout(String refreshToken) {
        log.info("Logout request");

        refreshTokenRepository.findByToken(refreshToken)
                .ifPresent(token -> {
                    token.revoke();
                    refreshTokenRepository.save(token);
                });
    }

    @Override
    @Transactional
    public void logoutAll(String userId) {
        log.info("Logout all devices: {}", userId);

        refreshTokenRepository.revokeAllUserTokens(userId, LocalDateTime.now());
    }

    @Override
    @Transactional
    public ForgotPasswordResponse forgotPassword(ForgotPasswordRequest request) {
        log.info("Forgot password request: {}", request.getEmail());

        // Buscar usuário por email
        User user = userRepository.findByEmailIgnoreCase(request.getEmail())
                .orElseThrow(() -> new BadRequestException("Usuário não encontrado"));

        // Gerar código OTP
        String otpCode = generateOTP();
        System.out.println("otpCode: " + otpCode);

        // Definir expiração (15 minutos)
        LocalDateTime expiresAt = LocalDateTime.now().plusMinutes(OTP_EXPIRATION_MINUTES);

        // Salvar código e expiração no usuário
        user.setResetPasswordCode(otpCode);
        user.setResetPasswordCodeExpiresAt(expiresAt);
        userRepository.save(user);

        // Enviar email com código OTP
        emailService.sendPasswordResetCode(user.getEmail(), otpCode, user.getName());

        log.info("Password reset code sent to: {}", user.getId());

        return ForgotPasswordResponse.builder()
                .message("Código de recuperação enviado para o email")
                .email(request.getEmail())
                .build();
    }

    /*@Override
    @Transactional(readOnly = true)
    public VerifyResetCodeResponse verifyResetCode(VerifyResetCodeRequest request) {
        log.info("Verify reset code request: {}", request.getEmail());

        // Buscar usuário por email
        User user = userRepository.findByEmailIgnoreCase(request.getEmail())
                .orElseThrow(() -> new BadRequestException("Usuário não encontrado"));

        // Verificar se existe código
        if (user.getResetPasswordCode() == null || user.getResetPasswordCodeExpiresAt() == null) {
            return VerifyResetCodeResponse.builder()
                    .message("Nenhum código de recuperação foi solicitado")
                    .valid(false)
                    .build();
        }

        // Verificar se o código expirou
        if (LocalDateTime.now().isAfter(user.getResetPasswordCodeExpiresAt())) {
            return VerifyResetCodeResponse.builder()
                    .message("Código expirado. Solicite um novo código")
                    .valid(false)
                    .build();
        }

        // Verificar se o código está correto
        if (!user.getResetPasswordCode().equals(request.getCode())) {
            return VerifyResetCodeResponse.builder()
                    .message("Código inválido")
                    .valid(false)
                    .build();
        }

        log.info("Reset code verified successfully for: {}", user.getId());

        return VerifyResetCodeResponse.builder()
                .message("Código válido")
                .valid(true)
                .build();
    }

     */
    @Override
    @Transactional
    public VerifyResetCodeResponse verifyResetCode(VerifyResetCodeRequest request) {
        log.info("Verify reset code request: {}", request.getEmail());

        User user = userRepository.findByEmailIgnoreCase(request.getEmail())
                .orElseThrow(() -> new BadRequestException("Usuário não encontrado"));

        if (user.getResetPasswordCode() == null || user.getResetPasswordCodeExpiresAt() == null) {
            return VerifyResetCodeResponse.builder()
                    .message("Nenhum código de verificação foi solicitado")
                    .valid(false)
                    .build();
        }

        if (LocalDateTime.now().isAfter(user.getResetPasswordCodeExpiresAt())) {
            return VerifyResetCodeResponse.builder()
                    .message("Código expirado. Solicite um novo código")
                    .valid(false)
                    .build();
        }

        if (!user.getResetPasswordCode().equals(request.getCode())) {
            return VerifyResetCodeResponse.builder()
                    .message("Código inválido")
                    .valid(false)
                    .build();
        }

        // Código válido! Se é cadastro novo, marcar email como verificado
        if (user.getPasswordHash() == null) {
            user.setEmailVerified(true);
            userRepository.save(user);
            log.info("Email verified for new user: {}", user.getId());
        }

        return VerifyResetCodeResponse.builder()
                .message("Código válido")
                .valid(true)
                .build();
    }

    @Override
    @Transactional
    public ResetPasswordResponse resetPassword(ResetPasswordRequest request) {
        log.info("Reset password request: {}", request.getEmail());

        // Buscar usuário por email
        User user = userRepository.findByEmailIgnoreCase(request.getEmail())
                .orElseThrow(() -> new BadRequestException("Usuário não encontrado"));

        // Verificar se existe código
        if (user.getResetPasswordCode() == null || user.getResetPasswordCodeExpiresAt() == null) {
            throw new BadRequestException("Nenhum código de recuperação foi solicitado");
        }

        // Verificar se o código expirou
        if (LocalDateTime.now().isAfter(user.getResetPasswordCodeExpiresAt())) {
            throw new BadRequestException("Código expirado. Solicite um novo código");
        }

        // Verificar se o código está correto
        if (!user.getResetPasswordCode().equals(request.getCode())) {
            throw new BadRequestException("Código inválido");
        }

        // Atualizar senha
        user.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));

        // Limpar código OTP após uso
        user.setResetPasswordCode(null);
        user.setResetPasswordCodeExpiresAt(null);

        userRepository.save(user);

        // ⭐ LOGOUT GLOBAL AUTOMÁTICO - Invalidar todas as sessões ativas por segurança
        logoutAll(user.getId());

        log.info("Password reset successful for: {} - All sessions invalidated", user.getId());

        return ResetPasswordResponse.builder()
                .message("Senha redefinida com sucesso")
                .build();
    }

    @Override
    @Transactional
    public void verifyEmail(VerifyEmailRequest request) {
        log.info("Verify email request");

        // TODO: Validar token
        // TODO: Marcar email como verificado
        throw new UnsupportedOperationException("Verify email não implementado ainda");
    }

    @Override
    @Transactional
    public void resendVerificationEmail(String email) {
        log.info("Resend verification email: {}", email);

        userRepository.findByEmailIgnoreCase(email)
                .ifPresent(user -> {
                    if (user.getEmailVerified()) {
                        throw new BadRequestException("Email já verificado");
                    }

                    // TODO: Gerar novo token
                    // TODO: Enviar email
                    log.info("Verification email resent: {}", user.getId());
                });
    }

    /**
     * Métodos auxiliares privados
     */

    /**
     * Gera código OTP de 6 dígitos
     */
    private String generateOTP() {
        SecureRandom random = new SecureRandom();
        int otp = 100000 + random.nextInt(900000); // Gera número entre 100000 e 999999
        return String.valueOf(otp);
    }

    private void saveRefreshToken(String userId, String token) {
        RefreshToken refreshToken = RefreshToken.create(userId, token, REFRESH_TOKEN_EXPIRATION_DAYS);
        refreshTokenRepository.save(refreshToken);
    }

    private Tenant createPersonalTenant(User user) {
        String slug = generateTenantSlug(user.getName());

        Tenant tenant = Tenant.builder()
                .name("Pessoal - " + user.getName())
                .slug(slug)
                .type(Tenant.TenantType.PERSONAL)
                .plan(Tenant.TenantPlan.FREE)
                .ownerId(user.getId())
                .isActive(true)
                .build();

        tenant.startSubscription(Tenant.TenantPlan.FREE, 365); // 1 ano de trial

        return tenantRepository.save(tenant);
    }

    private String generateTenantSlug(String name) {
        String baseSlug = name.toLowerCase()
                .replaceAll("[^a-z0-9\\s-]", "")
                .replaceAll("\\s+", "-")
                .replaceAll("-+", "-")
                .trim();

        String slug = baseSlug;
        int counter = 1;

        while (tenantRepository.existsBySlug(slug)) {
            slug = baseSlug + "-" + counter++;
        }

        return slug;
    }

    private AuthResponse buildAuthResponse(
            User user,
            String accessToken,
            String refreshToken,
            Tenant tenant,
            TenantMember.MemberRole role
    ) {
        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn((long) ACCESS_TOKEN_EXPIRATION_MINUTES * 60) // segundos
                .user(AuthResponse.UserDTO.builder()
                        .id(user.getId())
                        .name(user.getName())
                        .email(user.getEmail())
                        .phone(user.getPhone())
                        .avatarUrl(user.getAvatarUrl())
                        .emailVerified(user.getEmailVerified())
                        .phoneVerified(user.getPhoneVerified())
                        .build())
                .defaultTenant(AuthResponse.TenantDTO.builder()
                        .id(tenant.getId())
                        .name(tenant.getName())
                        .slug(tenant.getSlug())
                        .type(tenant.getType().name())
                        .plan(tenant.getPlan().name())
                        .role(role.name())
                        .build())
                .build();
    }

    // com/moneylegal/auth/service/AuthServiceImpl.java

    /**
     * PASSO 2: INSERT - Criar usuário básico e enviar OTP
     */
    /*@Override
    @Transactional
    public PreRegisterResponse preRegister(PreRegisterRequest request) {
        log.info("Pre-register (INSERT) - email: {}", request.getEmail());

        // Validar se email já existe
        if (userRepository.existsByEmailIgnoreCase(request.getEmail())) {
            throw new BadRequestException("Email já cadastrado");
        }

        // INSERT: Criar usuário básico (sem senha, inativo)
        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail().toLowerCase())
                .passwordHash(null) // SEM SENHA
                .emailVerified(false)
                .isActive(false) // INATIVO
                .build();

        user = userRepository.save(user);

        // Gerar e salvar código OTP
        String otpCode = generateOTP();
        LocalDateTime expiresAt = LocalDateTime.now().plusMinutes(OTP_EXPIRATION_MINUTES);

        user.setResetPasswordCode(otpCode);
        user.setResetPasswordCodeExpiresAt(expiresAt);
        userRepository.save(user);

        // Enviar email com OTP
        emailService.sendPasswordResetCode(user.getEmail(), otpCode, user.getName());

        log.info("User created (INSERT) and OTP sent: {}", user.getId());

        return PreRegisterResponse.builder()
                .message("Código de verificação enviado para o email")
                .email(user.getEmail())
                .build();
    }
     */

    /**
     * PASSO 2: INSERT ou REENVIO - Criar usuário básico e enviar OTP
     */
    @Override
    @Transactional
    public PreRegisterResponse preRegister(PreRegisterRequest request) {
        log.info("Pre-register (INSERT/UPDATE) - email: {}", request.getEmail());

        // Buscar se usuário já existe
        User user = userRepository.findByEmailIgnoreCase(request.getEmail()).orElse(null);

        if (user != null) {
            // Usuário existe - verificar se já completou cadastro
            if (user.getPasswordHash() != null) {
                // ❌ Tem senha = cadastro completo
                throw new BadRequestException("Email já cadastrado");
            }

            // ✅ Sem senha = pré-cadastro abandonado, pode reenviar OTP
            log.info("User exists without password, resending OTP: {}", user.getId());

            // Atualizar nome se mudou
            user.setName(request.getName());

        } else {
            // Usuário não existe - criar novo
            log.info("Creating new user (INSERT)");

            user = User.builder()
                    .name(request.getName())
                    .email(request.getEmail().toLowerCase())
                    .passwordHash(null) // SEM SENHA
                    .emailVerified(false)
                    .isActive(false) // INATIVO
                    .build();

            user = userRepository.save(user);
        }

        // Gerar e salvar código OTP (novo ou atualizar existente)
        String otpCode = generateOTP();
        LocalDateTime expiresAt = LocalDateTime.now().plusMinutes(OTP_EXPIRATION_MINUTES);

        user.setResetPasswordCode(otpCode);
        user.setResetPasswordCodeExpiresAt(expiresAt);
        userRepository.save(user);

        // Enviar email com OTP
        //emailService.sendEmailVerificationOtp(user.getEmail(), otpCode, user.getName());

        log.info("OTP sent to: {}", user.getId());

        return PreRegisterResponse.builder()
                .message("Código de verificação enviado para o email")
                .email(user.getEmail())
                .build();
    }
    /**
     * PASSO 3: UPDATE ou INSERT - Completar cadastro
     */
    @Override
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        log.info("Registrando novo usuário: {}", request.getEmail());

        // Buscar se usuário já existe
        User user = userRepository.findByEmailIgnoreCase(request.getEmail()).orElse(null);

        if (user != null) {
            // ✅ USUÁRIO EXISTE → UPDATE (fluxo com OTP do pre-register)
            log.info("Usuário encontrado, fazendo UPDATE: {}", user.getId());

            // Validar se email foi verificado
            if (!user.getEmailVerified()) {
                throw new BadRequestException("Email não verificado. Valide o código OTP primeiro.");
            }

            // Validar se já completou cadastro antes
            if (user.getPasswordHash() != null && user.getIsActive()) {
                throw new BadRequestException("Cadastro já foi completado");
            }

            // Validar telefone se fornecido
            if (request.getPhone() != null && !request.getPhone().equals(user.getPhone())) {
                if (userRepository.existsByPhone(request.getPhone())) {
                    throw new BadRequestException("Telefone já cadastrado");
                }
            }

            // UPDATE: Completar cadastro
            user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
            user.setPhone(request.getPhone());
            user.setIsActive(true);

            // Limpar código OTP após completar cadastro
            user.setResetPasswordCode(null);
            user.setResetPasswordCodeExpiresAt(null);

            user = userRepository.save(user);
            log.info("Cadastro completado com UPDATE: {}", user.getId());

        } else {
            // ❌ USUÁRIO NÃO EXISTE → INSERT (fluxo antigo sem OTP)
            log.info("Usuário não encontrado, criando novo (INSERT - fluxo antigo)");

            // Validar se email já existe (checagem dupla por segurança)
            if (userRepository.existsByEmailIgnoreCase(request.getEmail())) {
                throw new BadRequestException("Email já cadastrado");
            }

            // Validar telefone se fornecido
            if (request.getPhone() != null && userRepository.existsByPhone(request.getPhone())) {
                throw new BadRequestException("Telefone já cadastrado");
            }

            // INSERT: Criar usuário novo
            user = User.builder()
                    .name(request.getName())
                    .email(request.getEmail().toLowerCase())
                    .phone(request.getPhone())
                    .passwordHash(passwordEncoder.encode(request.getPassword()))
                    .emailVerified(false)
                    .phoneVerified(false)
                    .isActive(true)
                    .build();

            user = userRepository.save(user);
            log.info("Usuário criado com sucesso (INSERT): {}", user.getId());

            // TODO: Enviar email de verificação
            emailService.sendVerificationEmail(user.getEmail(), "verificationToken", user.getName());
        }

        // Criar tenant pessoal automático
        Tenant personalTenant = createPersonalTenant(user);

        // Criar membership (OWNER)
        TenantMember membership = TenantMember.builder()
                .tenantId(personalTenant.getId())
                .userId(user.getId())
                .role(TenantMember.MemberRole.OWNER)
                .isActive(true)
                .build();
        tenantMemberRepository.save(membership);

        // Gerar tokens
        String accessToken = jwtTokenProvider.generateAccessToken(user.getId());
        String refreshToken = jwtTokenProvider.generateRefreshToken();

        // Salvar refresh token
        saveRefreshToken(user.getId(), refreshToken);

        // ENVIAR EMAIL DE BOAS-VINDAS (após tudo estar completo)
        try {
            emailService.sendWelcomeEmail(user.getEmail(), user.getName());
            log.info("Welcome email sent to new user: {}", user.getEmail());
        } catch (Exception e) {
            // Log mas não falha o cadastro se email der erro
            log.error("Failed to send welcome email to: {}", user.getEmail(), e);
        }

        // Construir response
        return buildAuthResponse(user, accessToken, refreshToken, personalTenant, TenantMember.MemberRole.OWNER);
    }

    @Override
    public boolean existsByEmailWithPassword(String email) {
        return userRepository.existsByEmailIgnoreCaseAndPasswordHashIsNotNull(email);
    }
}