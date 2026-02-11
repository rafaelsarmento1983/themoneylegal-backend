package com.moneylegal.profile.controller;

import com.moneylegal.profile.dto.*;
import com.moneylegal.profile.service.ProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/profile")
@RequiredArgsConstructor
@Slf4j
public class ProfileController {
    
    private final ProfileService profileService;
    
    /**
     * PASSO 1: Escolher tipo de cadastro (PF ou PJ)
     * POST /api/v1/profile/choose-type
     */
    @PostMapping("/choose-type")
    public ResponseEntity<ProfileResponseDTO> chooseType(
            @Valid @RequestBody ChooseTypeRequestDTO request,
            Authentication authentication
    ) {
        String userId = authentication.getName();
        log.info("POST /profile/choose-type - userId: {}, tipo: {}", userId, request.getTipo());
        
        ProfileResponseDTO response = profileService.chooseType(userId, request);
        return ResponseEntity.ok(response);
    }
    
    /**
     * PASSO 2A: Completar dados de Pessoa Física
     * POST /api/v1/profile/pessoa-fisica
     */
    @PostMapping("/pessoa-fisica")
    public ResponseEntity<ProfileResponseDTO> completePessoaFisica(
            @Valid @RequestBody CompletePessoaFisicaRequestDTO request,
            Authentication authentication
    ) {
        String userId = authentication.getName();
        log.info("POST /profile/pessoa-fisica - userId: {}", userId);
        
        ProfileResponseDTO response = profileService.completePessoaFisica(userId, request);
        return ResponseEntity.ok(response);
    }
    
    /**
     * PASSO 2B: Completar dados de Pessoa Jurídica
     * POST /api/v1/profile/pessoa-juridica
     */
    @PostMapping("/pessoa-juridica")
    public ResponseEntity<ProfileResponseDTO> completePessoaJuridica(
            @Valid @RequestBody CompletePessoaJuridicaRequestDTO request,
            Authentication authentication
    ) {
        String userId = authentication.getName();
        log.info("POST /profile/pessoa-juridica - userId: {}", userId);
        
        ProfileResponseDTO response = profileService.completePessoaJuridica(userId, request);
        return ResponseEntity.ok(response);
    }
    
    /**
     * PASSO 3: Completar endereço (finaliza cadastro)
     * POST /api/v1/profile/address
     */
    @PostMapping("/address")
    public ResponseEntity<ProfileResponseDTO> completeAddress(
            @Valid @RequestBody CompleteAddressRequestDTO request,
            Authentication authentication
    ) {
        String userId = authentication.getName();
        log.info("POST /profile/address - userId: {}", userId);
        
        ProfileResponseDTO response = profileService.completeAddress(userId, request);
        return ResponseEntity.ok(response);
    }
    
    /**
     * Buscar perfil completo do usuário autenticado
     * GET /api/v1/profile/me
     */
    /*@GetMapping("/me")
    public ResponseEntity<ProfileResponseDTO> getMyProfile(Authentication authentication) {
        String userId = authentication.getName();
        log.info("GET /profile/me - userId: {}", userId);
        
        ProfileResponseDTO response = profileService.getMyProfile(userId);
        return ResponseEntity.ok(response);
    }*/

    @GetMapping("/me")
    public ResponseEntity<?> getMyProfile(Authentication authentication) {
        String userId = authentication.getName();
        log.info("GET /profile/me - userId: {}", userId);

        return profileService.getMyProfile(userId)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() ->
                        ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                                Map.of(
                                        "code", "PROFILE_NOT_FOUND",
                                        "message", "Perfil ainda não criado"
                                )
                        )
                );
    }


    /**
     * Consultar CEP via ViaCEP
     * GET /api/v1/profile/cep/{cep}
     */
    @GetMapping("/cep/{cep}")
    public ResponseEntity<ViaCepResponseDTO> consultarCep(@PathVariable String cep) {
        log.info("GET /profile/cep/{}", cep);
        
        ViaCepResponseDTO response = profileService.consultarCep(cep);
        return ResponseEntity.ok(response);
    }
}
