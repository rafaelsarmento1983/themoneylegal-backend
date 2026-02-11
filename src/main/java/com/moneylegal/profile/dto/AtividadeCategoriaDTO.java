package com.moneylegal.profile.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AtividadeCategoriaDTO {
    private String id;
    private String label;
    private String icon;
    private List<AtividadeItemDTO> items;
}
