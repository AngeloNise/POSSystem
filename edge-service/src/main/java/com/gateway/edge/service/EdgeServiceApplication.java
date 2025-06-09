package com.gateway.edge.service;

import io.netty.resolver.DefaultAddressResolverGroup;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
import org.springframework.web.util.pattern.PathPatternParser;
import reactor.netty.http.client.HttpClient;

import java.util.Collections;
import java.util.List;

@EnableDiscoveryClient
@EnableFeignClients
@SpringBootApplication
//@SpringBootApplication(exclude = { SecurityAutoConfiguration.class })
public class EdgeServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(EdgeServiceApplication.class, args);
	}

	@Bean
	public BCryptPasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public HttpClient httpClient() {
		return HttpClient.create().resolver(DefaultAddressResolverGroup.INSTANCE);
	}
	@Bean
	public CorsWebFilter corsFilter() {
		CorsConfiguration config = new CorsConfiguration();
		config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PUT","OPTIONS"));
		config.setAllowedHeaders(List.of("Authorization", "Cache-Control", "Content-Type","X-XSRF-TOKEN"));
		config.setExposedHeaders(List.of("Authorization","Set-Cookie"));
		config.setAllowCredentials(true);
//		config.setAllowedOriginPatterns(List.of("http://localhost:3000/"));
//      config.setAllowedOriginPatterns(Collections.singletonList("http://180.232.124.189:3000/"));
		config.setAllowedOriginPatterns(List.of("http://localhost:3000/","http://180.232.124.189:3000/",
				"http://180.232.124.188:3389/", "http://localhost:3389/",
				"http://180.232.124.188:8081/", "http://localhost:8081/" ,
				"http://180.232.124.189:3389/", "http://180.232.124.189:8081/",
				"http://180.232.124.189:8888/", "http://180.232.124.189:3001/",
				"http://209.146.26.68:3389/", "http://209.146.26.68:8081/",
				"http://209.146.30.91:3389/", "http://209.146.30.91:8081/"
				)
		);
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource(
				new PathPatternParser());
		source.registerCorsConfiguration("/**", config);

		return new CorsWebFilter(source);
	}
}
