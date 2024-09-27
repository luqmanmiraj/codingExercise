package com.example.codingexercise;

import com.example.codingexercise.gateway.dto.ProductPackageDto;
import com.example.codingexercise.model.ProductPackage;
import com.example.codingexercise.repository.PackageRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;

import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class PackageControllerTests {

    public static final String USER = "user";
    public static final String USER_PASSWD = "pass";

    @LocalServerPort
    private int port;

    private final TestRestTemplate restTemplate;
    private final PackageRepository packageRepository;

    @Autowired
    PackageControllerTests(TestRestTemplate restTemplate, PackageRepository packageRepository) {
        this.restTemplate = restTemplate;
        this.packageRepository = packageRepository;
    }

    @Test
    void createPackage() {
        ProductPackageDto newPackage = new ProductPackageDto();
        newPackage.setName("Test Name");
        newPackage.setDescription("Test Desc");
        newPackage.setProductIds(List.of("VqKb4tyj9V6i"));

        ResponseEntity<ProductPackageDto> created = restTemplate.withBasicAuth(USER, USER_PASSWD)
                .postForEntity("/packages", newPackage, ProductPackageDto.class);
        assertEquals(HttpStatus.OK, created.getStatusCode(), "Unexpected status code");
        ProductPackageDto createdBody = created.getBody();
        assertNotNull(createdBody, "Unexpected body");
        assertNotNull(createdBody.getId(), "ID should be generated");
        assertEquals("Test Name", createdBody.getName(), "Unexpected name");
        assertEquals("Test Desc", createdBody.getDescription(), "Unexpected description");
        assertEquals(List.of("VqKb4tyj9V6i"), createdBody.getProductIds(), "Unexpected products");

        ProductPackage productPackage = packageRepository.findById(createdBody.getId()).orElse(null);
        assertNotNull(productPackage, "Package should exist in repository");
        assertEquals(createdBody.getId(), productPackage.getId(), "Unexpected id");
        assertEquals(createdBody.getName(), productPackage.getName(), "Unexpected name");
        assertEquals(createdBody.getDescription(), productPackage.getDescription(), "Unexpected description");
        assertEquals(createdBody.getProductIds(), productPackage.getProductIds(), "Unexpected products");
    }

    @Test
    void createPackageWithEmptyProductList() {
        ProductPackageDto newPackage = new ProductPackageDto();
        newPackage.setName("Test Name");
        newPackage.setDescription("Test Desc");
        newPackage.setProductIds(List.of());

        ResponseEntity<String> response = restTemplate.withBasicAuth(USER, USER_PASSWD)
                .postForEntity("/packages", newPackage, String.class);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode(), "Expected BAD_REQUEST status");
        assertTrue(response.getBody().contains("There should be at least one product for a package"), "Expected error message about empty product list");
    }

    @Test
    void createPackageWithInvalidProductId() {
        ProductPackageDto newPackage = new ProductPackageDto();
        newPackage.setName("Test Name");
        newPackage.setDescription("Test Desc");
        newPackage.setProductIds(List.of("invalid"));

        ResponseEntity<String> response = restTemplate.withBasicAuth(USER, USER_PASSWD)
                .postForEntity("/packages", newPackage, String.class);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode(), "Expected BAD_REQUEST status");
        assertTrue(response.getBody().contains("Provided productId: invalid not found"), "Expected error message about invalid product ID");
    }

    @Test
    void getPackage() {
        ProductPackage productPackage = new ProductPackage();
        productPackage.setName("Test Name 2");
        productPackage.setDescription("Test Desc 2");
        productPackage.setProductIds(List.of("DXSQpv6XVeJm"));
        productPackage = packageRepository.save(productPackage);

        ResponseEntity<ProductPackageDto> fetched = restTemplate.withBasicAuth(USER, USER_PASSWD)
                .getForEntity("/packages/{id}", ProductPackageDto.class, productPackage.getId());
        assertEquals(HttpStatus.OK, fetched.getStatusCode(), "Unexpected status code");
        ProductPackageDto fetchedBody = fetched.getBody();
        assertNotNull(fetchedBody, "Unexpected body");
        assertEquals(productPackage.getId(), fetchedBody.getId(), "Unexpected id");
        assertEquals(productPackage.getName(), fetchedBody.getName(), "Unexpected name");
        assertEquals(productPackage.getDescription(), fetchedBody.getDescription(), "Unexpected description");
        assertThat(fetchedBody.getProductIds()).containsExactlyInAnyOrderElementsOf(productPackage.getProductIds());
    }

    @Test
    void getPackageWithInvalidId() {
        ResponseEntity<String> response = restTemplate.withBasicAuth(USER, USER_PASSWD)
                .getForEntity("/packages/{id}", String.class, 999L);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode(), "Expected NOT_FOUND status");
        assertTrue(response.getBody().contains("Provided packageId: 999 not found"), "Expected error message about invalid package ID");
    }

    @Test
    void getPackageWithCurrency() {
        ProductPackage productPackage = new ProductPackage();
        productPackage.setName("Test Name 3");
        productPackage.setDescription("Test Desc 3");
        productPackage.setProductIds(List.of("7dgX6XzU3Wds"));
        productPackage = packageRepository.save(productPackage);

        ResponseEntity<ProductPackageDto> fetched = restTemplate.withBasicAuth(USER, USER_PASSWD)
                .getForEntity("/packages/{id}?currency={currency}", ProductPackageDto.class, productPackage.getId(), "GBP");
        assertEquals(HttpStatus.OK, fetched.getStatusCode(), "Unexpected status code");
        ProductPackageDto fetchedBody = fetched.getBody();
        assertNotNull(fetchedBody, "Unexpected body");
        assertEquals(productPackage.getId(), fetchedBody.getId(), "Unexpected id");
        assertEquals(productPackage.getName(), fetchedBody.getName(), "Unexpected name");
        assertEquals(productPackage.getDescription(), fetchedBody.getDescription(), "Unexpected description");
        assertThat(productPackage.getProductIds()).containsExactlyInAnyOrderElementsOf(fetchedBody.getProductIds());
        assertNotNull(fetchedBody.getProducts(), "Products should be populated");
        assertTrue(fetchedBody.getPrice() > 0, "Price should be set");
    }

    @Test
    void getPackageWithInvalidCurrency() {
        ProductPackage productPackage = new ProductPackage();
        productPackage.setName("Test Name 6");
        productPackage.setDescription("Test Desc 6");
        productPackage.setProductIds(List.of("7dgX6XzU3Wds"));
        productPackage = packageRepository.save(productPackage);

        ResponseEntity<String> response = restTemplate.withBasicAuth(USER, USER_PASSWD)
                .getForEntity("/packages/{id}?currency={currency}", String.class, productPackage.getId(), "INVALID");
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode(), "Expected BAD_REQUEST status");
        assertTrue(response.getBody().contains("Invalid currency"), "Expected error message about invalid currency");
    }

    @Test
    void listPackages() {
        packageRepository.deleteAll();
        ProductPackage productPackage1 = new ProductPackage();
        productPackage1.setName("Test Name 1");
        productPackage1.setDescription("Test Desc 1");
        productPackage1.setProductIds(List.of("PKM5pGAh9yGm"));
        packageRepository.save(productPackage1);

        ProductPackage productPackage2 = new ProductPackage();
        productPackage2.setName("Test Name 2");
        productPackage2.setDescription("Test Desc 2");
        productPackage2.setProductIds(List.of("7Hv0hA2nmci7"));
        packageRepository.save(productPackage2);

        ResponseEntity<List<ProductPackageDto>> fetched = restTemplate.withBasicAuth(USER, USER_PASSWD)
                .exchange("/packages", HttpMethod.GET, null, new ParameterizedTypeReference<List<ProductPackageDto>>() {});
        assertEquals(HttpStatus.OK, fetched.getStatusCode(), "Unexpected status code");
        List<ProductPackageDto> fetchedBody = fetched.getBody();
        assertNotNull(fetchedBody, "Unexpected body");
        assertEquals(2, fetchedBody.size(), "Should return 2 packages");
    }

    @Test
    void updatePackage() {
        ProductPackage productPackage = new ProductPackage();
        productPackage.setName("Test Name 4");
        productPackage.setDescription("Test Desc 4");
        productPackage.setProductIds(List.of("PKM5pGAh9yGm"));
        productPackage = packageRepository.save(productPackage);

        ProductPackageDto updatedDto = new ProductPackageDto();
        updatedDto.setId(productPackage.getId());
        updatedDto.setName("Updated Name");
        updatedDto.setDescription("Updated Desc");
        updatedDto.setProductIds(List.of("7Hv0hA2nmci7"));

        HttpEntity<ProductPackageDto> request = new HttpEntity<>(updatedDto);
        ResponseEntity<ProductPackageDto> updated = restTemplate.withBasicAuth(USER, USER_PASSWD)
                .exchange("/packages/{id}", HttpMethod.PUT, request, ProductPackageDto.class, productPackage.getId());
        assertEquals(HttpStatus.OK, updated.getStatusCode(), "Unexpected status code");
        ProductPackageDto updatedBody = updated.getBody();
        assertNotNull(updatedBody, "Unexpected body");
        assertEquals(productPackage.getId(), updatedBody.getId(), "Unexpected id");
        assertEquals("Updated Name", updatedBody.getName(), "Unexpected name");
        assertEquals("Updated Desc", updatedBody.getDescription(), "Unexpected description");
        assertEquals(List.of("7Hv0hA2nmci7"), updatedBody.getProductIds(), "Unexpected products");
        assertThat(List.of("7Hv0hA2nmci7")).containsExactlyInAnyOrderElementsOf(updatedBody.getProductIds());
    }

    @Test
    void updateNonExistentPackage() {
        ProductPackageDto updatedDto = new ProductPackageDto();
        updatedDto.setId(999L);
        updatedDto.setName("Updated Name");
        updatedDto.setDescription("Updated Desc");
        updatedDto.setProductIds(List.of("7Hv0hA2nmci7"));

        HttpEntity<ProductPackageDto> request = new HttpEntity<>(updatedDto);
        ResponseEntity<String> response = restTemplate.withBasicAuth(USER, USER_PASSWD)
                .exchange("/packages/{id}", HttpMethod.PUT, request, String.class, 999L);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode(), "Expected NOT_FOUND status");
        assertTrue(response.getBody().contains("Provided packageId: 999 not found"), "Expected error message about non-existent package");
    }

    @Test
    void deletePackage() {
        ProductPackage productPackage = new ProductPackage();
        productPackage.setName("Test Name 5");
        productPackage.setDescription("Test Desc 5");
        productPackage.setProductIds(List.of("7Hv0hA2nmci7"));
        productPackage = packageRepository.save(productPackage);

        ResponseEntity<Void> deleted = restTemplate.withBasicAuth(USER, USER_PASSWD)
                .exchange("/packages/{id}", HttpMethod.DELETE, null, Void.class, productPackage.getId());
        assertEquals(HttpStatus.OK, deleted.getStatusCode(), "Unexpected status code");
        
        // Verify that the package is deleted
        ResponseEntity<ProductPackageDto> fetched = restTemplate.withBasicAuth(USER, USER_PASSWD)
                .getForEntity("/packages/{id}", ProductPackageDto.class, productPackage.getId());
        assertEquals(HttpStatus.NOT_FOUND, fetched.getStatusCode(), "Package should not exist after deletion");
    }

    @Test
    void deleteNonExistentPackage() {
        ResponseEntity<String> response = restTemplate.withBasicAuth(USER, USER_PASSWD)
                .exchange("/packages/{id}", HttpMethod.DELETE, null, String.class, 999L);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode(), "Expected NOT_FOUND status");
        assertTrue(response.getBody().contains("Provided packageId: 999 not found"), "Expected error message about non-existent package");
    }
}
