package com.example.codingexercise.controller;

import com.example.codingexercise.gateway.dto.ProductPackageDto;
import com.example.codingexercise.service.PackageService;
import com.example.codingexercise.validation.CurrencyValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/packages")
public class PackageController {

    private final PackageService packageService;

    @Autowired
    public PackageController(PackageService packageService) {
        this.packageService = packageService;
    }

    @RequestMapping(method = RequestMethod.POST)
    public ProductPackageDto create(@RequestBody ProductPackageDto productPackage) {
        return packageService.create(productPackage);
    }

    @GetMapping("/{id}")
    public ProductPackageDto get(@PathVariable Long id, @RequestParam(required = false) String currency) {
        if (currency != null) {
            CurrencyValidator.validate(currency);
            return packageService.getProductPackageWithCurrency(id, currency);
        }
        return packageService.getProductPackage(id);
    }

    @GetMapping
    public List<ProductPackageDto> getAll() {
        return packageService.getAllProductPackages();
    }

    @PutMapping("/{id}")
    public ProductPackageDto update(@PathVariable Long id, @RequestBody ProductPackageDto productPackage) {
        return packageService.update(id, productPackage);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        packageService.delete(id);
    }
}
