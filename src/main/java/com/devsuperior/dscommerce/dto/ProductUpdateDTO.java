package com.devsuperior.dscommerce.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.util.ArrayList;
import java.util.List;

public class ProductUpdateDTO {

    @NotBlank(message = "Campo obrigatório")
    @Size(min = 3, max = 80, message = "Nome precisa ter de 3 a 80 caracteres")
    private String name;

    @NotBlank(message = "Campo obrigatório")
    @Size(min = 10, message = "Descrição precisa ter pelo menos 10 caracteres")
    private String description;

    @NotNull(message = "Campo obrigatório")
    @Positive(message = "O preço deve ser positivo")
    private Double price;

    private String imgUrl;

    @NotNull(message = "Deve ter pelo menos uma categoria")
    private List<Long> categoryIds = new ArrayList<>();

    public ProductUpdateDTO() {
    }

    public ProductUpdateDTO(String name, String description, Double price, String imgUrl) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.imgUrl = imgUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public List<Long> getCategoryIds() {
        return categoryIds;
    }

    public void setCategoryIds(List<Long> categoryIds) {
        this.categoryIds = categoryIds;
    }
}