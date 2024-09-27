package com.example.codingexercise.repository;

import com.example.codingexercise.model.ProductPackage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PackageRepository extends JpaRepository<ProductPackage, Long> {
}