package com.moneylegal.auth.dto;

import jakarta.validation.constraints.*;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {
    @NotBlank(message = "Nome é obrigatório")
    @Size(min = 3, max = 100)
    private String name;

    @NotBlank(message = "Email é obrigatório")
    @Email(message = "Email inválido")
    private String email;

    @Size(max = 20)
    private String phone;

    @NotBlank(message = "Senha é obrigatória")
    @Size(min = 8, max = 100)
    private String password;
}