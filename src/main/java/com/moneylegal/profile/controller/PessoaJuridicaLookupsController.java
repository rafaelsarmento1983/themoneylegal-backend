package com.moneylegal.profile.controller;

import com.moneylegal.profile.dto.*;
import com.moneylegal.profile.service.PessoaJuridicaLookupsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/lookups/pessoa-juridica")
@RequiredArgsConstructor
public class PessoaJuridicaLookupsController {

    private final PessoaJuridicaLookupsService lookups;

    @GetMapping("/portes")
    public ResponseEntity<List<LookupOptionDTO>> portes() {
        return ResponseEntity.ok(lookups.listPortes());
    }

    @GetMapping("/naturezas")
    public ResponseEntity<List<LookupOptionDTO>> naturezas() {
        return ResponseEntity.ok(lookups.listNaturezas());
    }

    // Hierárquico: categorias + itens
    @GetMapping("/atividades")
    public ResponseEntity<List<AtividadeCategoriaDTO>> atividades() {
        return ResponseEntity.ok(lookups.listAtividades());
    }
}
