package com.example.codingexercise.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

import com.example.codingexercise.gateway.dto.ProductPackageDto;

@Getter
@Setter
@Entity
public class ProductPackage {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String name;
    private String description;

    @ElementCollection(fetch = FetchType.EAGER)
    private List<String> productIds;

    public static ProductPackageDto toDto(ProductPackage productPackage) {
        ProductPackageDto dto = new ProductPackageDto();
        dto.setId(productPackage.getId());
        dto.setName(productPackage.getName());
        dto.setDescription(productPackage.getDescription());
        dto.setProductIds(productPackage.getProductIds());
        return dto;
    }
}
