package com.moneylegal.auth.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VerifyEmailRequest {
    @NotBlank(message = "Token é obrigatório")
    private String token;
}