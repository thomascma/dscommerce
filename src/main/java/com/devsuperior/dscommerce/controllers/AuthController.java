package com.devsuperior.dscommerce.controllers;

import com.devsuperior.dscommerce.dto.CredentialsDTO;
import com.devsuperior.dscommerce.dto.TokenDTO;
import com.devsuperior.dscommerce.dto.UserDTO;
import com.devsuperior.dscommerce.services.AuthService;
import com.devsuperior.dscommerce.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private UserService userService;

    @PostMapping(value = "/login")
    public ResponseEntity<TokenDTO> login(@RequestBody CredentialsDTO body) {
        TokenDTO dto = authService.authenticate(body);
        return ResponseEntity.ok(dto);
    }

    @GetMapping(value = "/me")
    public ResponseEntity<UserDTO> getMe() {
        UserDTO dto = userService.getMe();
        return ResponseEntity.ok(dto);
    }
}
