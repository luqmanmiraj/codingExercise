package com.example.codingexercise.gateway.dto;


import lombok.Getter;
import lombok.Setter;

import java.util.List;

import com.example.codingexercise.model.ProductPackage;

@Getter
@Setter
public class ProductPackageDto {

    private Long id;
    private String name;
    private String description;
    private List<String> productIds;
    private List<Product> products;
    private double price;

    public static ProductPackage toEntity(ProductPackageDto dto) {
        ProductPackage productPackage = new ProductPackage();
        productPackage.setId(dto.getId());
        productPackage.setName(dto.getName());
        productPackage.setDescription(dto.getDescription());
        productPackage.setProductIds(dto.getProductIds());
        return productPackage;
    }
}
