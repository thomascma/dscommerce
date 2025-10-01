package com.devsuperior.dscommerce.config.customgrant;

import org.springframework.lang.Nullable;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2AuthorizationGrantAuthenticationToken;
import java.util.Map;

public class CustomPasswordAuthenticationToken extends OAuth2AuthorizationGrantAuthenticationToken {

    private final String username;
    private final String password;

    public CustomPasswordAuthenticationToken(Authentication clientPrincipal, @Nullable Map<String, Object> additionalParameters, String username, String password) {
        super(new AuthorizationGrantType("password"), clientPrincipal, additionalParameters);
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return this.username;
    }

    public String getPassword() {
        return this.password;
    }
}
