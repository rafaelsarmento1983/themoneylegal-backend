package com.moneylegal.tenant.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AcceptInvitationDTO {

    @NotBlank(message = "Código do convite é obrigatório")
    private String code;
}
