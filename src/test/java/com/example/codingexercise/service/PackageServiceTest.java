package com.example.codingexercise.service;

import com.example.codingexercise.exception.RecordNotFoundException;
import com.example.codingexercise.gateway.CurrencyServiceGateway;
import com.example.codingexercise.gateway.ProductServiceGateway;
import com.example.codingexercise.gateway.dto.Product;
import com.example.codingexercise.gateway.dto.ProductPackageDto;
import com.example.codingexercise.model.ProductPackage;
import com.example.codingexercise.repository.PackageRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class PackageServiceTests {

    @Mock
    private PackageRepository packageRepository;

    @Mock
    private ProductServiceGateway productServiceGateway;

    @Mock
    private CurrencyServiceGateway currencyServiceGateway;

    @InjectMocks
    private PackageService packageService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createPackage_success() {
        ProductPackageDto productPackageDto = new ProductPackageDto();
        productPackageDto.setName("Test Name");
        productPackageDto.setDescription("Test Desc");
        productPackageDto.setProductIds(Arrays.asList("prod1"));

        Product product = new Product("prod1", "Prod 1", 100 );

        when(productServiceGateway.getProduct("prod1")).thenReturn(product);
        when(packageRepository.save(any(ProductPackage.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ProductPackageDto result = packageService.create(productPackageDto);

        assertNotNull(result);
        assertEquals("Test Name", result.getName());
        assertEquals("Test Desc", result.getDescription());
        assertEquals(1, result.getProductIds().size());
        assertEquals(100.0, result.getPrice());
    }

    @Test
    void createPackage_emptyProductIds_throwsException() {
        ProductPackageDto productPackageDto = new ProductPackageDto();
        productPackageDto.setName("Test Name");
        productPackageDto.setDescription("Test Desc");
        productPackageDto.setProductIds(Arrays.asList());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            packageService.create(productPackageDto);
        });

        assertEquals("There should be at least one product for a package", exception.getMessage());
    }

    @Test
    void createPackage_invalidProductId_throwsException() {
        ProductPackageDto productPackageDto = new ProductPackageDto();
        productPackageDto.setName("Test Name");
        productPackageDto.setDescription("Test Desc");
        productPackageDto.setProductIds(Arrays.asList("invalidProdId"));

        when(productServiceGateway.getProduct("invalidProdId")).thenReturn(null);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            packageService.create(productPackageDto);
        });

        assertEquals("Provided productId: invalidProdId not found", exception.getMessage());
    }

    @Test
    void getProductPackageWithCurrency_success() {
        ProductPackage productPackage = new ProductPackage();
        productPackage.setId(1L);
        productPackage.setName("Test Name");
        productPackage.setDescription("Test Desc");
        productPackage.setProductIds(Arrays.asList("prod1"));

        Product product = new Product("prod1", "Prod 1", 100 );

        when(packageRepository.findById(1L)).thenReturn(Optional.of(productPackage));
        when(productServiceGateway.getProduct("prod1")).thenReturn(product);
        when(currencyServiceGateway.convertUsdTo(100.0, "EUR")).thenReturn(85.0);

        ProductPackageDto result = packageService.getProductPackageWithCurrency(1L, "EUR");

        assertNotNull(result);
        assertEquals(85.0, result.getPrice());
    }

    @Test
    void getProductPackage_success() {
        ProductPackage productPackage = new ProductPackage();
        productPackage.setId(1L);
        productPackage.setName("Test Name");
        productPackage.setDescription("Test Desc");
        productPackage.setProductIds(Arrays.asList("prod1"));

        Product product = new Product("prod1", "Prod 1", 100 );

        when(packageRepository.findById(1L)).thenReturn(Optional.of(productPackage));
        when(productServiceGateway.getProduct("prod1")).thenReturn(product);

        ProductPackageDto result = packageService.getProductPackage(1L);

        assertNotNull(result);
        assertEquals(100.0, result.getPrice());
    }

    @Test
    void getProductPackage_notFound_throwsException() {
        when(packageRepository.findById(1L)).thenReturn(Optional.empty());

        RecordNotFoundException exception = assertThrows(RecordNotFoundException.class, () -> {
            packageService.getProductPackage(1L);
        });

        assertEquals("Provided packageId: 1 not found", exception.getMessage());
    }

    @Test
    void getAllProductPackages_success() {
        ProductPackage productPackage1 = new ProductPackage();
        productPackage1.setId(1L);
        productPackage1.setName("Test Name 1");
        productPackage1.setDescription("Test Desc 1");
        productPackage1.setProductIds(Arrays.asList("prod1"));

        ProductPackage productPackage2 = new ProductPackage();
        productPackage2.setId(2L);
        productPackage2.setName("Test Name 2");
        productPackage2.setDescription("Test Desc 2");
        productPackage2.setProductIds(Arrays.asList("prod2"));

        Product product1 = new Product("prod1", "Prod 1", 100 );
        Product product2 = new Product("prod2", "Prod 2", 200 );

        when(packageRepository.findAll()).thenReturn(Arrays.asList(productPackage1, productPackage2));
        when(productServiceGateway.getProduct("prod1")).thenReturn(product1);
        when(productServiceGateway.getProduct("prod2")).thenReturn(product2);

        List<ProductPackageDto> result = packageService.getAllProductPackages();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(100.0, result.get(0).getPrice());
        assertEquals(200.0, result.get(1).getPrice());
    }

    @Test
    void updatePackage_success() {
        ProductPackage existingPackage = new ProductPackage();
        existingPackage.setId(1L);
        existingPackage.setName("Test Name");
        existingPackage.setDescription("Test Desc");
        existingPackage.setProductIds(Arrays.asList("prod1"));

        ProductPackageDto updatedDto = new ProductPackageDto();
        updatedDto.setId(1L);
        updatedDto.setName("Updated Name");
        updatedDto.setDescription("Updated Desc");
        updatedDto.setProductIds(Arrays.asList("prod2"));

        Product product = new Product("prod2", "Prod 1", 200 );

        when(packageRepository.findById(1L)).thenReturn(Optional.of(existingPackage));
        when(packageRepository.save(any(ProductPackage.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(productServiceGateway.getProduct("prod2")).thenReturn(product);

        ProductPackageDto result = packageService.update(1L, updatedDto);

        assertNotNull(result);
        assertEquals("Updated Name", result.getName());
        assertEquals("Updated Desc", result.getDescription());
        assertEquals(200.0, result.getPrice());
    }

    @Test
    void updatePackage_notFound_throwsException() {
        ProductPackageDto updatedDto = new ProductPackageDto();
        updatedDto.setId(1L);
        updatedDto.setName("Updated Name");
        updatedDto.setDescription("Updated Desc");
        updatedDto.setProductIds(Arrays.asList("prod2"));

        when(packageRepository.findById(1L)).thenReturn(Optional.empty());

        RecordNotFoundException exception = assertThrows(RecordNotFoundException.class, () -> {
            packageService.update(1L, updatedDto);
        });

        assertEquals("Provided packageId: 1 not found", exception.getMessage());
    }

    @Test
    void deletePackage_success() {
        ProductPackage existingPackage = new ProductPackage();
        existingPackage.setId(1L);
        existingPackage.setName("Test Name");
        existingPackage.setDescription("Test Desc");
        existingPackage.setProductIds(Arrays.asList("prod1"));

        when(packageRepository.findById(1L)).thenReturn(Optional.of(existingPackage));

        packageService.delete(1L);

        verify(packageRepository, times(1)).delete(existingPackage);
    }

    @Test
    void deletePackage_notFound_throwsException() {
        when(packageRepository.findById(1L)).thenReturn(Optional.empty());

        RecordNotFoundException exception = assertThrows(RecordNotFoundException.class, () -> {
            packageService.delete(1L);
        });

        assertEquals("Provided packageId: 1 not found", exception.getMessage());
    }
}