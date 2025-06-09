package com.example.productsalesservice;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.example.productsalesservice.repository")
public class ProductSalesServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ProductSalesServiceApplication.class, args);
    }

}
