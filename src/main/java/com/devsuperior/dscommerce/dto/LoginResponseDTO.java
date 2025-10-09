package com.devsuperior.dscommerce.dto;

import java.time.Instant;
import java.util.List;

public class LoginResponseDTO {
    private String accessToken;
    private String tokenType;
    private Long expiresIn;
    private Instant expiresAt;
    private List<String> scopes;

    public LoginResponseDTO() {
    }

    public LoginResponseDTO(String accessToken, String tokenType, Long expiresIn, Instant expiresAt, List<String> scopes) {
        this.accessToken = accessToken;
        this.tokenType = tokenType;
        this.expiresIn = expiresIn;
        this.expiresAt = expiresAt;
        this.scopes = scopes;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getTokenType() {
        return tokenType;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }

    public Long getExpiresIn() {
        return expiresIn;
    }

    public void setExpiresIn(Long expiresIn) {
        this.expiresIn = expiresIn;
    }

    public Instant getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(Instant expiresAt) {
        this.expiresAt = expiresAt;
    }

    public List<String> getScopes() {
        return scopes;
    }

    public void setScopes(List<String> scopes) {
        this.scopes = scopes;
    }
}
