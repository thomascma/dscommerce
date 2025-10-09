package com.devsuperior.dscommerce.controllers;

import com.devsuperior.dscommerce.dto.LoginRequestDTO;
import com.devsuperior.dscommerce.dto.LoginResponseDTO;
import com.devsuperior.dscommerce.services.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping(value = "/login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody LoginRequestDTO loginRequest) {
        LoginResponseDTO response = authService.login(loginRequest);
        return ResponseEntity.ok(response);
    }
}
