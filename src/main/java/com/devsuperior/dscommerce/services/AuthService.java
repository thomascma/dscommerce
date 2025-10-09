package com.devsuperior.dscommerce.services;

import com.devsuperior.dscommerce.dto.LoginRequestDTO;
import com.devsuperior.dscommerce.dto.LoginResponseDTO;
import com.devsuperior.dscommerce.entities.User;
import com.devsuperior.dscommerce.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuthenticationManager authenticationManager;

    public LoginResponseDTO login(LoginRequestDTO loginRequest) {
        try {
            // Autenticar usuário
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getEmail(),
                            loginRequest.getPassword()
                    )
            );

            // Buscar usuário
            User user = userRepository.findByEmail(loginRequest.getEmail());
            if (user == null) {
                throw new RuntimeException("Usuário não encontrado");
            }

            // Gerar token JWT (simulado - em produção usar JWT real)
            String accessToken = generateJwtToken(user);
            
            // Criar resposta
            LoginResponseDTO response = new LoginResponseDTO();
            response.setAccessToken(accessToken);
            response.setTokenType("Bearer");
            response.setExpiresIn(3600L); // 1 hora
            response.setExpiresAt(Instant.now().plusSeconds(3600));
            response.setScopes(List.of("read", "write"));

            return response;

        } catch (AuthenticationException e) {
            throw new RuntimeException("Credenciais inválidas", e);
        }
    }

    private String generateJwtToken(User user) {
        // Simulação de token JWT - em produção usar biblioteca JWT real
        return "jwt_token_" + user.getId() + "_" + System.currentTimeMillis();
    }
}
