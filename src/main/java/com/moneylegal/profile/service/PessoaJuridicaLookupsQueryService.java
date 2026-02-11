package com.moneylegal.profile.service;

import com.moneylegal.profile.dto.PessoaJuridicaLookupsResponseDTO;
import com.moneylegal.profile.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PessoaJuridicaLookupsQueryService {

    private final PessoaJuridicaPorteEmpresaRepository porteRepo;
    private final PessoaJuridicaNaturezaJuridicaRepository naturezaRepo;
    private final PessoaJuridicaAtividadeCategoriaRepository catRepo;
    private final PessoaJuridicaAtividadeItemRepository itemRepo;

    public PessoaJuridicaLookupsResponseDTO getAll() {
        var porte = porteRepo.findAllByIsActiveTrueOrderBySortOrderAscLabelAsc()
                .stream()
                .map(p -> PessoaJuridicaLookupsResponseDTO.OptionDTO.builder()
                        .id(p.getId())
                        .label(p.getLabel())
                        .icon(p.getIcon())
                        .build())
                .collect(Collectors.toList());

        var natureza = naturezaRepo.findAllByIsActiveTrueOrderBySortOrderAscLabelAsc()
                .stream()
                .map(n -> PessoaJuridicaLookupsResponseDTO.OptionDTO.builder()
                        .id(n.getId())
                        .label(n.getLabel())
                        .icon(n.getIcon())
                        .build())
                .collect(Collectors.toList());

        var atividade = catRepo.findAllByIsActiveTrueOrderBySortOrderAscLabelAsc()
                .stream()
                .map(cat -> {
                    var items = itemRepo.findAllByIsActiveTrueAndCategoryIdOrderBySortOrderAscLabelAsc(cat.getId())
                            .stream()
                            .map(it -> PessoaJuridicaLookupsResponseDTO.AtividadeItemDTO.builder()
                                    .id(it.getId())
                                    .label(it.getLabel())
                                    .icon(it.getIcon())
                                    .build())
                            .collect(Collectors.toList());

                    return PessoaJuridicaLookupsResponseDTO.AtividadeCategoriaDTO.builder()
                            .id(cat.getId())
                            .label(cat.getLabel())
                            .icon(cat.getIcon())
                            .items(items)
                            .build();
                })
                .collect(Collectors.toList());

        return PessoaJuridicaLookupsResponseDTO.builder()
                .porteEmpresa(porte)
                .naturezaJuridica(natureza)
                .atividade(atividade)
                .build();
    }
}
