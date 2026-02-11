package com.moneylegal.profile.service;

import com.moneylegal.exception.BadRequestException;
import com.moneylegal.profile.dto.*;
import com.moneylegal.profile.model.*;
import com.moneylegal.profile.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PessoaJuridicaLookupsService {

    private final PessoaJuridicaPorteEmpresaRepository porteRepo;
    private final PessoaJuridicaNaturezaJuridicaRepository naturezaRepo;
    private final PessoaJuridicaAtividadeCategoriaRepository categoriaRepo;
    private final PessoaJuridicaAtividadeItemRepository itemRepo;

    // =====================================================
    // LISTAGENS (para o frontend)
    // =====================================================

    public List<LookupOptionDTO> listPortes() {
        return porteRepo.findAllByIsActiveTrueOrderBySortOrderAscLabelAsc()
                .stream()
                .map(p -> LookupOptionDTO.builder()
                        .id(p.getId())
                        .label(p.getLabel())
                        .icon(p.getIcon())
                        .build())
                .collect(Collectors.toList());
    }

    public List<LookupOptionDTO> listNaturezas() {
        return naturezaRepo.findAllByIsActiveTrueOrderBySortOrderAscLabelAsc()
                .stream()
                .map(n -> LookupOptionDTO.builder()
                        .id(n.getId())
                        .label(n.getLabel())
                        .icon(n.getIcon())
                        .build())
                .collect(Collectors.toList());
    }

    public List<AtividadeCategoriaDTO> listAtividades() {
        return categoriaRepo.findAllByIsActiveTrueOrderBySortOrderAscLabelAsc()
                .stream()
                .map(cat -> {
                    var items = itemRepo.findAllByIsActiveTrueAndCategoryIdOrderBySortOrderAscLabelAsc(cat.getId())
                            .stream()
                            .map(it -> AtividadeItemDTO.builder()
                                    .id(it.getId())
                                    .label(it.getLabel())
                                    .icon(it.getIcon())
                                    .build())
                            .collect(Collectors.toList());

                    return AtividadeCategoriaDTO.builder()
                            .id(cat.getId())
                            .label(cat.getLabel())
                            .icon(cat.getIcon())
                            .items(items)
                            .build();
                })
                .collect(Collectors.toList());
    }

    // =====================================================
    // VALIDAÇÕES (usadas pelo ProfileService)
    // =====================================================

    public PessoaJuridicaPorteEmpresa requirePorteAtivo(String id) {
        if (id == null) return null;

        return porteRepo.findById(id)
                .filter(PessoaJuridicaPorteEmpresa::getIsActive)
                .orElseThrow(() ->
                        new BadRequestException("Porte da empresa inválido ou inativo")
                );
    }

    public PessoaJuridicaNaturezaJuridica requireNaturezaAtiva(String id) {
        if (id == null) return null;

        return naturezaRepo.findById(id)
                .filter(PessoaJuridicaNaturezaJuridica::getIsActive)
                .orElseThrow(() ->
                        new BadRequestException("Natureza jurídica inválida ou inativa")
                );
    }

    public PessoaJuridicaAtividadeItem requireAtividadeItemAtivo(String id) {
        if (id == null) return null;

        return itemRepo.findById(id)
                .filter(PessoaJuridicaAtividadeItem::getIsActive)
                .orElseThrow(() ->
                        new BadRequestException("Atividade principal inválida ou inativa")
                );
    }
}
