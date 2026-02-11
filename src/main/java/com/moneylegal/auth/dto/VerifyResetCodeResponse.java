// backend/src/main/java/com/moneylegal/auth/dto/VerifyResetCodeResponse.java
package com.moneylegal.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VerifyResetCodeResponse {
    private String message;
    private boolean valid;
}