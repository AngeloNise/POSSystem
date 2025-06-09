package com.example.purchasehistory;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.example.purchasehistory.repository")
public class PosHistoryServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(PosHistoryServiceApplication.class, args);
	}
}
