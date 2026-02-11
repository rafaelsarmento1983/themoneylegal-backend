package com.moneylegal.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO de resposta para verificação de email
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmailCheckResponse {

    /**
     * Se o email existe no sistema
     */
    private boolean exists;

    /**
     * Mensagem descritiva
     */
    private String message;
}
