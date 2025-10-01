package com.devsuperior.dscommerce.config.customgrant;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.lang.Nullable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.security.web.authentication.AuthenticationConverter;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.HashMap;
import java.util.Map;

/**
 * Conversor customizado para autenticação via Password Grant.
 * Implementa o padrão OAuth2 Password Grant Type conforme RFC 6749.
 * 
 * @author DevSuperior
 * @version 1.0
 */
public class CustomPasswordAuthenticationConverter implements AuthenticationConverter {

    /**
     * Converte a requisição HTTP em um token de autenticação personalizado.
     * 
     * @param request A requisição HTTP contendo os parâmetros OAuth2
     * @return CustomPasswordAuthenticationToken se o grant_type for "password", null caso contrário
     */
    @Nullable
    @Override
    public Authentication convert(HttpServletRequest request) {
        String grantType = request.getParameter(OAuth2ParameterNames.GRANT_TYPE);
        
        // Verifica se o grant_type é "password"
        if (!"password".equals(grantType)) {
            return null;
        }

        // Obtém o cliente autenticado do contexto de segurança
        Authentication clientPrincipal = SecurityContextHolder.getContext().getAuthentication();
        
        // Extrai os parâmetros da requisição
        MultiValueMap<String, String> parameters = getParameters(request);
        
        // Extrai username e password dos parâmetros
        String username = parameters.getFirst(OAuth2ParameterNames.USERNAME);
        String password = parameters.getFirst(OAuth2ParameterNames.PASSWORD);
        
        // Cria mapa de parâmetros adicionais (excluindo os parâmetros padrão)
        Map<String, Object> additionalParameters = new HashMap<>();
        parameters.forEach((key, value) -> {
            if (!key.equals(OAuth2ParameterNames.GRANT_TYPE) && 
                !key.equals(OAuth2ParameterNames.CLIENT_ID) && 
                !key.equals(OAuth2ParameterNames.USERNAME) && 
                !key.equals(OAuth2ParameterNames.PASSWORD)) {
                additionalParameters.put(key, value.get(0));
            }
        });

        // Retorna o token de autenticação personalizado
        return new CustomPasswordAuthenticationToken(clientPrincipal, additionalParameters, username, password);
    }

    /**
     * Extrai os parâmetros da requisição HTTP.
     * 
     * @param request A requisição HTTP
     * @return MultiValueMap contendo todos os parâmetros
     */
    private static MultiValueMap<String, String> getParameters(HttpServletRequest request) {
        Map<String, String[]> parameterMap = request.getParameterMap();
        MultiValueMap<String, String> parameters = new LinkedMultiValueMap<>(parameterMap.size());
        
        parameterMap.forEach((key, values) -> {
            if (values.length > 0) {
                for (String value : values) {
                    parameters.add(key, value);
                }
            }
        });
        
        return parameters;
    }
}