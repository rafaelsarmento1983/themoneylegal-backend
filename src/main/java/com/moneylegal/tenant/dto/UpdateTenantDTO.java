package com.moneylegal.tenant.dto;

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
public class UpdateTenantDTO {

    @Size(min = 3, max = 100, message = "Nome deve ter entre 3 e 100 caracteres")
    private String name;

    private String logoUrl;

    @Pattern(regexp = "^#([A-Fa-f0-9]{6})$", message = "Cor primária deve estar no formato hexadecimal (#RRGGBB)")
    private String primaryColor;
}
