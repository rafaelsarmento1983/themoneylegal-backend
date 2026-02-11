package com.moneylegal.profile.dto;

import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PessoaJuridicaLookupsResponseDTO {

    private List<OptionDTO> porteEmpresa;
    private List<OptionDTO> naturezaJuridica;
    private List<AtividadeCategoriaDTO> atividade;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class OptionDTO {
        private String id;
        private String label;
        private String icon; // emoji ou lucide string
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class AtividadeCategoriaDTO {
        private String id;
        private String label;
        private String icon;
        private List<AtividadeItemDTO> items;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class AtividadeItemDTO {
        private String id;
        private String label;
        private String icon;
    }
}
