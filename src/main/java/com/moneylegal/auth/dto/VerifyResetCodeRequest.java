// backend/src/main/java/com/moneylegal/auth/dto/VerifyResetCodeRequest.java
package com.moneylegal.auth.dto;

import jakarta.validation.constraints.Email;
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
public class VerifyResetCodeRequest {

    @NotBlank(message = "Email é obrigatório")
    @Email(message = "Email inválido")
    private String email;

    @NotBlank(message = "Código é obrigatório")
    @Size(min = 6, max = 6, message = "Código deve ter 6 dígitos")
    @Pattern(regexp = "^[0-9]{6}$", message = "Código deve conter apenas números")
    private String code;
}