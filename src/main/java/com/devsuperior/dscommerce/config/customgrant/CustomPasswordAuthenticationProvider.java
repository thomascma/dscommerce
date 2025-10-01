package com.devsuperior.dscommerce.config.customgrant;

import java.security.Principal;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.*;
import org.springframework.security.oauth2.server.authorization.OAuth2Authorization;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2AccessTokenAuthenticationToken;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2ClientAuthenticationToken;
import org.springframework.security.oauth2.server.authorization.context.AuthorizationServerContextHolder;
import org.springframework.security.oauth2.server.authorization.token.*;
import org.springframework.util.Assert;

/**
 * Provider de autenticação personalizado para o Password Grant Type.
 * Implementa a lógica de autenticação e geração de tokens para o fluxo
 * de autenticação via username/password conforme RFC 6749.
 * 
 * @author DevSuperior
 * @version 1.0
 */
public class CustomPasswordAuthenticationProvider implements org.springframework.security.authentication.AuthenticationProvider {
    
    private static final String ERROR_URI = "https://datatracker.ietf.org/doc/html/rfc6749#section-5.2";
    
    private final AuthenticationManager authenticationManager;
    private final OAuth2AuthorizationService authorizationService;
    private final OAuth2TokenGenerator<? extends OAuth2Token> tokenGenerator;

    /**
     * Construtor do provider de autenticação personalizado.
     * 
     * @param authenticationManager Gerenciador de autenticação do Spring Security
     * @param authorizationService Serviço de autorização OAuth2
     * @param tokenGenerator Gerador de tokens OAuth2
     */
    public CustomPasswordAuthenticationProvider(AuthenticationManager authenticationManager, 
                                              OAuth2AuthorizationService authorizationService, 
                                              OAuth2TokenGenerator<? extends OAuth2Token> tokenGenerator) {
        Assert.notNull(authorizationService, "authorizationService cannot be null");
        Assert.notNull(tokenGenerator, "tokenGenerator cannot be null");
        this.authenticationManager = authenticationManager;
        this.authorizationService = authorizationService;
        this.tokenGenerator = tokenGenerator;
    }

    /**
     * Autentica o usuário e gera os tokens OAuth2.
     * 
     * @param authentication O token de autenticação personalizado
     * @return Token de autenticação com access token
     */
    @Override
    public Authentication authenticate(Authentication authentication) {
        CustomPasswordAuthenticationToken customPasswordAuthenticationToken = (CustomPasswordAuthenticationToken) authentication;
        
        // Valida e obtém o cliente autenticado
        OAuth2ClientAuthenticationToken clientPrincipal = getAuthenticatedClientElseThrowInvalidClient(customPasswordAuthenticationToken);
        
        // Cria token de autenticação username/password
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = 
            new UsernamePasswordAuthenticationToken(
                customPasswordAuthenticationToken.getUsername(), 
                customPasswordAuthenticationToken.getPassword()
            );
        
        // Autentica o usuário usando o AuthenticationManager
        Authentication usernamePasswordAuthentication = authenticationManager.authenticate(usernamePasswordAuthenticationToken);
        
        // Cria a autorização OAuth2
        var registeredClient = clientPrincipal.getRegisteredClient();
        if (registeredClient == null) {
            throw new OAuth2AuthenticationException("invalid_client");
        }
        
        OAuth2Authorization.Builder authorizationBuilder = OAuth2Authorization.withRegisteredClient(registeredClient)
                .principalName(usernamePasswordAuthentication.getName())
                .authorizationGrantType(new AuthorizationGrantType("password"))
                .attribute(Principal.class.getName(), usernamePasswordAuthentication);
        
        // Configura o contexto para geração de tokens
        DefaultOAuth2TokenContext.Builder tokenContextBuilder = DefaultOAuth2TokenContext.builder()
                .registeredClient(registeredClient)
                .principal(usernamePasswordAuthentication)
                .authorizationServerContext(AuthorizationServerContextHolder.getContext())
                .authorization(authorizationBuilder.build())
                .authorizedScopes(registeredClient.getScopes())
                .authorizationGrantType(new AuthorizationGrantType("password"))
                .authorizationGrant(customPasswordAuthenticationToken);
        
        OAuth2TokenContext tokenContext = tokenContextBuilder.build();
        
        // Gera o access token
        OAuth2Token generatedAccessToken = this.tokenGenerator.generate(tokenContext);
        if (generatedAccessToken == null) {
            throw new OAuth2AuthenticationException(
                new OAuth2Error("server_error", "The token generator failed to generate the access token.", ERROR_URI)
            );
        }
        
        // Cria o token de autenticação final
        OAuth2AccessTokenAuthenticationToken accessTokenAuthentication = 
            new OAuth2AccessTokenAuthenticationToken(
                clientPrincipal.getRegisteredClient(), 
                clientPrincipal, 
                (OAuth2AccessToken) generatedAccessToken
            );
        
        // Salva a autorização no serviço
        authorizationBuilder.token(accessTokenAuthentication.getAccessToken(), (metadata) -> {
            // Adiciona metadados do token se necessário
        });
        
        OAuth2Authorization authorization = authorizationBuilder.build();
        this.authorizationService.save(authorization);
        
        return accessTokenAuthentication;
    }

    /**
     * Verifica se este provider suporta o tipo de autenticação fornecido.
     * 
     * @param authentication Classe de autenticação a ser verificada
     * @return true se suporta, false caso contrário
     */
    @Override
    public boolean supports(Class<?> authentication) {
        return CustomPasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }

    /**
     * Valida e retorna o cliente autenticado ou lança exceção se inválido.
     * 
     * @param authentication A autenticação a ser validada
     * @return O cliente autenticado
     * @throws OAuth2AuthenticationException Se o cliente não for válido
     */
    private static OAuth2ClientAuthenticationToken getAuthenticatedClientElseThrowInvalidClient(Authentication authentication) {
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