// backend/src/main/java/com/moneylegal/auth/dto/ForgotPasswordResponse.java
package com.moneylegal.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ForgotPasswordResponse {
    private String message;
    private String email;
}