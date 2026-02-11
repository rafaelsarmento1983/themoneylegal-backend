package com.moneylegal.tenant.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateTenantDTO {

    @NotBlank(message = "Nome é obrigatório")
    @Size(min = 3, max = 100, message = "Nome deve ter entre 3 e 100 caracteres")
    private String name;

    @NotBlank(message = "Tipo é obrigatório")
    @Pattern(regexp = "PERSONAL|FAMILY|BUSINESS", message = "Tipo deve ser PERSONAL, FAMILY ou BUSINESS")
    private String type;

    @Pattern(regexp = "FREE|PREMIUM|ENTERPRISE", message = "Plano deve ser FREE, PREMIUM ou ENTERPRISE")
    private String plan;

    private String logoUrl;

    @Pattern(regexp = "^#([A-Fa-f0-9]{6})$", message = "Cor primária deve estar no formato hexadecimal (#RRGGBB)")
    private String primaryColor;
}
