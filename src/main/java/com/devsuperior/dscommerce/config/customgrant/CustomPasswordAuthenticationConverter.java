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

public class CustomPasswordAuthenticationConverter implements AuthenticationConverter {

    @Nullable
    @Override
    public Authentication convert(HttpServletRequest request) {
        AuthorizationGrantType grantType = new AuthorizationGrantType(request.getParameter(OAuth2ParameterNames.GRANT_TYPE));
        if (!AuthorizationGrantType.PASSWORD.equals(grantType)) {
            return null;
        }
        Authentication clientPrincipal = SecurityContextHolder.getContext().getAuthentication();
        MultiValueMap<String, String> parameters = getParameters(request);
        String username = parameters.getFirst(OAuth2ParameterNames.USERNAME);
        String password = parameters.getFirst(OAuth2ParameterNames.PASSWORD);
        Map<String, Object> additionalParameters = new HashMap<>();
        parameters.forEach((key, value) -> {
            if (!key.equals(OAuth2ParameterNames.GRANT_TYPE) &&
                    !key.equals(OAuth2ParameterNames.CLIENT_ID) &&
                    !key.equals(OAuth2ParameterNames.USERNAME) &&
                    !key.equals(OAuth2ParameterNames.PASSWORD)) {
                additionalParameters.put(key, value.get(0));
            }
        });

        return new CustomPasswordAuthenticationToken(clientPrincipal, additionalParameters, username, password);
    }

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
