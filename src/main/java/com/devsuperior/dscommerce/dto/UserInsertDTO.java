package com.devsuperior.dscommerce.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public class UserInsertDTO {

    @NotBlank(message = "Campo obrigatório")
    @Size(min = 3, max = 80, message = "Nome precisa ter de 3 a 80 caracteres")
    private String name;

    @NotBlank(message = "Campo obrigatório")
    @Email(message = "Favor entrar com um email válido")
    private String email;

    @NotBlank(message = "Campo obrigatório")
    @Size(min = 8, message = "Senha deve ter pelo menos 8 caracteres")
    private String password;

    @NotBlank(message = "Campo obrigatório")
    private String phone;

    @NotNull(message = "Campo obrigatório")
    @PastOrPresent(message = "A data de nascimento não pode ser futura")
    private LocalDate birthDate;

    public UserInsertDTO() {
    }

    public UserInsertDTO(String name, String email, String password, String phone, LocalDate birthDate) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.phone = phone;
        this.birthDate = birthDate;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }
}