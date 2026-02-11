// backend/src/main/java/com/moneylegal/auth/dto/ForgotPasswordRequest.java
package com.moneylegal.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ForgotPasswordRequest {

    @NotBlank(message = "Email é obrigatório")
    @Email(message = "Email inválido")
    private String email;
}