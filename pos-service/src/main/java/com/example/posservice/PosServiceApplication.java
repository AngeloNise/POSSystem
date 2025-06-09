package com.example.posservice;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.example.posservice.repository")
public class PosServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(PosServiceApplication.class, args);
	}
}
