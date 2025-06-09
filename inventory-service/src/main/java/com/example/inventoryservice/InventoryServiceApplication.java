package com.example.inventoryservice;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableDiscoveryClient
@EnableAsync
public class InventoryServiceApplication {

    @Autowired
    private static ApplicationContext context;
    @Autowired
    public void context(ApplicationContext context) { InventoryServiceApplication.context = context; }

    public static void main(String[] args) {
        SpringApplication.run(InventoryServiceApplication.class, args);

        try {

        } catch (Exception e) {
            e.printStackTrace();
        }
    }



}