package com.moneylegal.tenant.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InviteMemberDTO {

    @NotBlank(message = "Email é obrigatório")
    @Email(message = "Email inválido")
    private String email;

    @NotBlank(message = "Role é obrigatória")
    @Pattern(regexp = "VIEWER|MEMBER|MANAGER|ADMIN", message = "Role deve ser VIEWER, MEMBER, MANAGER ou ADMIN")
    private String role;

    private String message; // Mensagem personalizada no convite
}
