package com.devsuperior.dscommerce.dto;

import com.devsuperior.dscommerce.entities.User;

import java.util.ArrayList;
import java.util.List;

public class UserDTO {

    private Long id;
    private String name;
    private String email;
    private List<String> roles = new ArrayList<>();

    public UserDTO() {
    }

    public UserDTO(User entity) {
        id = entity.getId();
        name = entity.getName();
        email = entity.getEmail();
        entity.getRoles().forEach(role -> roles.add(role.getAuthority()));
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public List<String> getRoles() {
        return roles;
    }
}
