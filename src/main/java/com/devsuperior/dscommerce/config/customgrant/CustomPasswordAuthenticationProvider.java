package com.devsuperior.dscommerce.config.customgrant;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2Token;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2AccessTokenAuthenticationToken;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2ClientAuthenticationToken;
import org.springframework.security.oauth2.server.authorization.context.AuthorizationServerContextHolder;
import org.springframework.security.oauth2.server.authorization.token.DefaultOAuth2TokenContext;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenContext;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenGenerator;
import org.springframework.util.Assert;

public class CustomPasswordAuthenticationProvider implements org.springframework.security.authentication.AuthenticationProvider {

    private static final String ERROR_URI = "https://datatracker.ietf.org/doc/html/rfc6749#section-5.2";
    private final AuthenticationManager authenticationManager;
    private final OAuth2AuthorizationService authorizationService;
    private final OAuth2TokenGenerator<? extends OAuth2Token> tokenGenerator;

    public CustomPasswordAuthenticationProvider(AuthenticationManager authenticationManager, OAuth2AuthorizationService authorizationService, OAuth2TokenGenerator<? extends OAuth2Token> tokenGenerator) {
        Assert.notNull(authorizationService, "authorizationService cannot be null");
        Assert.notNull(tokenGenerator, "tokenGenerator cannot be null");
        this.authenticationManager = authenticationManager;
        this.authorizationService = authorizationService;
        this.tokenGenerator = tokenGenerator;
    }

    @Override
    public org.springframework.security.core.Authentication authenticate(org.springframework.security.core.Authentication authentication) {
        System.out.println("=== CustomPasswordAuthenticationProvider DEBUG ===");
        System.out.println("Authentication received: " + authentication.getClass().getSimpleName());
        
        CustomPasswordAuthenticationToken passwordAuthentication = (CustomPasswordAuthenticationToken) authentication;
        System.out.println("Username: " + passwordAuthentication.getUsername());
        System.out.println("Password: " + passwordAuthentication.getPassword());
        
        OAuth2ClientAuthenticationToken clientPrincipal = getAuthenticatedClientElseThrowInvalidClient(passwordAuthentication);
        System.out.println("Client Principal: " + clientPrincipal.getRegisteredClient().getClientId());
        
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(passwordAuthentication.getUsername(), passwordAuthentication.getPassword());
        System.out.println("Calling authenticationManager.authenticate()...");
        
        org.springframework.security.core.Authentication usernamePasswordAuthentication = authenticationManager.authenticate(usernamePasswordAuthenticationToken);
        System.out.println("Authentication result: " + usernamePasswordAuthentication.isAuthenticated());
        System.out.println("Principal: " + usernamePasswordAuthentication.getPrincipal().getClass().getSimpleName());
        DefaultOAuth2TokenContext.Builder tokenContextBuilder = DefaultOAuth2TokenContext.builder()
                .registeredClient(clientPrincipal.getRegisteredClient())
                .principal(usernamePasswordAuthentication)
                .authorizationServerContext(AuthorizationServerContextHolder.getContext())
                .authorizationGrantType(new AuthorizationGrantType("password"))
                .authorizationGrant(passwordAuthentication);
        OAuth2TokenContext tokenContext = tokenContextBuilder.build();
        System.out.println("Generating JWT token...");
        OAuth2Token generatedToken = this.tokenGenerator.generate(tokenContext);
        System.out.println("Token generated: " + (generatedToken != null ? "SUCCESS" : "FAILED"));
        
        if (generatedToken == null) {
            System.out.println("ERROR: Token generation failed!");
            OAuth2Error error = new OAuth2Error("server_error", "The token generator failed to generate the access token.", ERROR_URI);
            throw new OAuth2AuthenticationException(error);
        }
        
        System.out.println("Token type: " + generatedToken.getClass().getSimpleName());
        OAuth2AccessTokenAuthenticationToken accessTokenAuthentication = new OAuth2AccessTokenAuthenticationToken(clientPrincipal.getRegisteredClient(), clientPrincipal, (org.springframework.security.oauth2.core.OAuth2AccessToken) generatedToken);
        org.springframework.security.oauth2.server.authorization.OAuth2Authorization.Builder authorizationBuilder = org.springframework.security.oauth2.server.authorization.OAuth2Authorization.withRegisteredClient(clientPrincipal.getRegisteredClient())
                .principalName(usernamePasswordAuthentication.getName())
                .authorizationGrantType(new AuthorizationGrantType("password"))
                .attribute("principal", usernamePasswordAuthentication);
        if (generatedToken instanceof org.springframework.security.oauth2.core.ClaimAccessor) {
            authorizationBuilder.token((org.springframework.security.oauth2.core.OAuth2AccessToken) generatedToken, (metadata) ->
                    metadata.put(org.springframework.security.oauth2.server.authorization.OAuth2Authorization.Token.CLAIMS_METADATA_NAME, ((org.springframework.security.oauth2.core.ClaimAccessor) generatedToken).getClaims()));
        } else {
            authorizationBuilder.accessToken((org.springframework.security.oauth2.core.OAuth2AccessToken) generatedToken);
        }
        org.springframework.security.oauth2.server.authorization.OAuth2Authorization authorization = authorizationBuilder.build();
        System.out.println("Saving authorization...");
        this.authorizationService.save(authorization);
        System.out.println("=== AUTHENTICATION SUCCESSFUL ===");
        System.out.println("Returning access token authentication");
        return accessTokenAuthentication;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return CustomPasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }

    private static OAuth2ClientAuthenticationToken getAuthenticatedClientElseThrowInvalidClient(org.springframework.security.core.Authentication authentication) {
        OAuth2ClientAuthenticationToken clientPrincipal = null;
        if (OAuth2ClientAuthenticationToken.class.isAssignableFrom(authentication.getPrincipal().getClass())) {
            clientPrincipal = (OAuth2ClientAuthenticationToken) authentication.getPrincipal();
        }
        if (clientPrincipal != null && clientPrincipal.isAuthenticated()) {
            return clientPrincipal;
        }
        throw new OAuth2AuthenticationException("invalid_client");
    }
}
