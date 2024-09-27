# Product Package Service

This is a Spring Boot application that manages product packages. It allows you to create, update, delete, and retrieve product packages, as well as convert package prices to different currencies.

## Features

- Create a new product package
- Retrieve a product package by ID
- Retrieve a product package by ID with price conversion to a specified currency
- Update an existing product package
- Delete a product package
- Retrieve all product packages

## Prerequisites

- Java 11 or higher
- Maven 3.6.3 or higher
- Docker (optional, for running dependencies)

## Getting Started

### Clone the Repository
- git clone https://github.com/yourusername/product-package-service.git

### Build the Application
- cd product-package-service
- mvn clean install

### Run the Application
- mvn spring-boot:run

## API Endpoints

### Create read update delete
- following is postman collection for testing the api import the collection in postman and test the apis

https://api.postman.com/collections/38618392-6baf5cc8-c895-4717-a2e6-6c0137417009?access_key=

## API Endpoints

### Create a new product package
- POST /packages
- Request Body: JSON object representing the new product package
- Response: JSON object representing the created product package

### Retrieve a product package by ID
- GET /packages/{id}
- Response: JSON object representing the product package

### Retrieve a product package by ID with price conversion
- GET /packages/{id}/convert?currency={currency}
- Response: JSON object representing the product package with converted price

### Update an existing product package
- PUT /packages/{id}
- Request Body: JSON object representing the updated product package
- Response: JSON object representing the updated product package

### Delete a product package
- DELETE /packages/{id}
- Response: Status code indicating the result of the operation

### Retrieve all product packages
- GET /packages
- Response: JSON array representing all product packages
