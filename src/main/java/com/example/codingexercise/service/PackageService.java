package com.example.codingexercise.service;

import com.example.codingexercise.exception.RecordNotFoundException;
import com.example.codingexercise.gateway.CurrencyServiceGateway;
import com.example.codingexercise.gateway.ProductServiceGateway;
import com.example.codingexercise.gateway.dto.Product;
import com.example.codingexercise.gateway.dto.ProductPackageDto;
import com.example.codingexercise.model.ProductPackage;
import com.example.codingexercise.repository.PackageRepository;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

@Service
public class PackageService {

    private final PackageRepository packageRepository;
    private final ProductServiceGateway productServiceGateway;
    private final CurrencyServiceGateway currencyServiceGateway;

    public PackageService(PackageRepository packageRepository, ProductServiceGateway productServiceGateway,
                CurrencyServiceGateway currencyServiceGateway) {
        this.packageRepository = packageRepository;
        this.productServiceGateway = productServiceGateway;
        this.currencyServiceGateway = currencyServiceGateway;
    }

    public ProductPackageDto create(ProductPackageDto productPackageDto) {
        if (CollectionUtils.isEmpty(productPackageDto.getProductIds())) {
            throw new IllegalArgumentException("There should be at least one product for a package");
        }

        productPackageDto.getProductIds().forEach(productId -> {
            if (productServiceGateway.getProduct(productId) == null) {
                throw new IllegalArgumentException("Provided productId: "+ productId + " not found");
            }
        });

        ProductPackage productPackage = packageRepository.save(ProductPackageDto.toEntity(productPackageDto));
        ProductPackageDto productPackageDtoRet = ProductPackage.toDto(productPackage);
        setProducts(productPackageDtoRet);
        return productPackageDtoRet;
    }

    public ProductPackageDto getProductPackageWithCurrency(Long id, String currency) {
        ProductPackageDto productPackage = getProductPackage(id);
        productPackage.setPrice(currencyServiceGateway.convertUsdTo(productPackage.getPrice(), currency));
        return productPackage;
    }

    public ProductPackageDto getProductPackage(Long id) {
        ProductPackage productPackage = get(id);
        if (productPackage == null) {
            throw new RecordNotFoundException("Provided packageId: "+ id + " not found");
        }
        ProductPackageDto productPackageDto = ProductPackage.toDto(get(id));
        setProducts(productPackageDto);
        return productPackageDto;
    }

    public List<ProductPackageDto> getAllProductPackages() {
        List<ProductPackage> productPackages = packageRepository.findAll();
        List<ProductPackageDto> productPackageDtos = new ArrayList<>();
        for (ProductPackage productPackage : productPackages) {
            ProductPackageDto dto = ProductPackage.toDto(productPackage);
            setProducts(dto);
            productPackageDtos.add(dto);
        }
        return productPackageDtos;
    }

    public ProductPackageDto update(Long id, ProductPackageDto productPackageDto) {
        ProductPackage existingPackage = get(id);
        if (existingPackage == null) {
            throw new RecordNotFoundException("Provided packageId: " + id + " not found");
        }

        productPackageDto.getProductIds().forEach(productId -> {
            if (productServiceGateway.getProduct(productId) == null) {
                throw new IllegalArgumentException("Provided productId: "+ productId + " not found");
            }
        });

        existingPackage.setName(productPackageDto.getName());
        existingPackage.setDescription(productPackageDto.getDescription());
        existingPackage.setProductIds(productPackageDto.getProductIds());

        ProductPackage updatedPackage = packageRepository.save(existingPackage);
        ProductPackageDto updatedDto = ProductPackage.toDto(updatedPackage);
        setProducts(updatedDto);
        return updatedDto;
    }

    public void delete(Long id) {
        ProductPackage existingPackage = get(id);
        if (existingPackage == null) {
            throw new RecordNotFoundException("Provided packageId: " + id + " not found");
        }
        packageRepository.delete(existingPackage);
    }

    private void setProducts(ProductPackageDto productPackageDto) {
        productPackageDto.setProducts(new ArrayList<>());
        productPackageDto.getProductIds().forEach(productId ->  {
            Product product = productServiceGateway.getProduct(productId);
            if (product != null) {
                productPackageDto.getProducts().add(product);
                productPackageDto.setPrice(productPackageDto.getPrice() + product.usdPrice());
            }
        });
    }

    public ProductPackage get(Long id) {
        return packageRepository.findById(id).orElseThrow(() -> new RecordNotFoundException("Provided packageId: " + id + " not found"));
    }
}
