package com.moneylegal.profile.controller;

import com.moneylegal.profile.dto.PessoaJuridicaLookupsResponseDTO;
import com.moneylegal.profile.service.PessoaJuridicaLookupsQueryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/profile/lookups")
@RequiredArgsConstructor
@Slf4j
public class ProfileLookupsController {

    private final PessoaJuridicaLookupsQueryService pjLookupsQueryService;

    // ✅ retorna porte + natureza + atividade(categorias + itens) num único payload
    @GetMapping("/pessoa-juridica")
    public ResponseEntity<PessoaJuridicaLookupsResponseDTO> getPessoaJuridicaLookups() {
        log.info("GET /profile/lookups/pessoa-juridica");
        return ResponseEntity.ok(pjLookupsQueryService.getAll());
    }
}
