package com.moneylegal.auth.dto;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResetPasswordRequest {
    //@NotBlank(message = "Token é obrigatório")
    //private String token;

    @NotBlank(message = "Email é obrigatório")
    @Email(message = "Email inválido")
    private String email;

    @NotBlank(message = "Código é obrigatório")
    @Size(min = 6, max = 6, message = "Código deve ter 6 dígitos")
    @Pattern(regexp = "^[0-9]{6}$", message = "Código deve conter apenas números")
    private String code;

    @NotBlank(message = "Nova senha é obrigatória")
    @Size(min = 8, message = "Senha deve ter no mínimo 8 caracteres")
    private String newPassword;
}