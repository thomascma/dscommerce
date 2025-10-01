package com.devsuperior.dscommerce.services;

import com.devsuperior.dscommerce.dto.CredentialsDTO;
import com.devsuperior.dscommerce.dto.TokenDTO;
import com.devsuperior.dscommerce.entities.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class AuthService {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtEncoder jwtEncoder;


    public TokenDTO authenticate(CredentialsDTO dto) {
        UsernamePasswordAuthenticationToken usernamePassword = new UsernamePasswordAuthenticationToken(
                dto.getEmail(), dto.getPassword());
        Authentication auth = authenticationManager.authenticate(usernamePassword);
        User user = (User) auth.getPrincipal();
        return new TokenDTO(generateToken(user));
    }

    private String generateToken(User user) {
        Instant now = Instant.now();
        long expiry = 3600L; // 1 hour

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("dscommerce")
                .issuedAt(now)
                .expiresAt(now.plusSeconds(expiry))
                .subject(user.getId().toString())
                .claim("scope", "read write")
                .build();

        return jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
    }
}
