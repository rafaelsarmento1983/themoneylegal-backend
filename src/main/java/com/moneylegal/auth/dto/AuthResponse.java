package com.moneylegal.auth.dto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {
    private String accessToken;
    private String refreshToken;
    private String tokenType;
    private Long expiresIn;
    private UserDTO user;
    private TenantDTO defaultTenant;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserDTO {
        private String id;
        private String name;
        private String email;
        private String phone;
        private String avatarUrl;
        private Boolean emailVerified;
        private Boolean phoneVerified;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TenantDTO {
        private String id;
        private String name;
        private String slug;
        private String type;
        private String plan;
        private String role;
    }
}