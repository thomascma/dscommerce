package com.devsuperior.dscommerce.config.customgrant;

import org.springframework.lang.Nullable;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2AuthorizationGrantAuthenticationToken;

import java.util.Map;

/**
 * Token de autenticação personalizado para o Password Grant Type.
 * Estende OAuth2AuthorizationGrantAuthenticationToken para suportar
 * autenticação via username/password conforme RFC 6749.
 * 
 * @author DevSuperior
 * @version 1.0
 */
public class CustomPasswordAuthenticationToken extends OAuth2AuthorizationGrantAuthenticationToken {
    
    private final String username;
    private final String password;

    /**
     * Construtor para criar um token de autenticação personalizado.
     * 
     * @param clientPrincipal O cliente autenticado
     * @param additionalParameters Parâmetros adicionais da requisição
     * @param username Nome de usuário para autenticação
     * @param password Senha para autenticação
     */
    public CustomPasswordAuthenticationToken(Authentication clientPrincipal, 
                                           @Nullable Map<String, Object> additionalParameters, 
                                           String username, 
                                           String password) {
        super(new AuthorizationGrantType("password"), clientPrincipal, additionalParameters);
        this.username = username;
        this.password = password;
    }

    /**
     * Retorna o nome de usuário.
     * 
     * @return O nome de usuário
     */
    public String getUsername() { 
        return this.username; 
    }

    /**
     * Retorna a senha.
     * 
     * @return A senha
     */
    public String getPassword() { 
        return this.password; 
    }
}