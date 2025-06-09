package com.gateway.edge.service.config;

/**
 * Created by Default on 21/04/2022.
 */
import com.gateway.edge.service.security.AuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class SpringCloudConfig {

    private final AuthenticationFilter filter;

    @Bean
    public RouteLocator gatewayRoutes(RouteLocatorBuilder builder){
        return builder.routes()
                .route("auth-service", r -> r.path("/auth/**")
                        .filters(f -> f.filter(filter))
                        .uri("lb://auth-service"))
                .route("system-service", r -> r.path("/resource/**","/system/**","/fhuser/**",
                                "/contactassets/**","/projects/**","/announcements/**","/te-datadict-list/**") //"/redis/**",
                        .filters(f -> f.filter(filter))
                        .uri("lb://system-service"))
                .route("pos-service", r -> r.path("/auth/**","api/transaction/**")
                        .filters(f -> f.filter(filter))
                        .uri("lb://pos-service"))
                .route("poshistory-service", r -> r.path("/auth/**", "/api/purchase-record/**", "/receipt/**")
                        .filters(f -> f.filter(filter))
                        .uri("lb://poshistory-service"))

                .route("inventory-service", r -> r.path("/inventory/**","/productSerial/**")
                        .filters(f -> f.filter(filter))
                        .uri("lb://inventory-service"))
                .route("purchase-service", r -> r.path("/ledger/**", "/receipt/**","/test/**","/search/**") //"/redis/**",
                        .filters(f -> f.filter(filter))
                        .uri("lb://purchase-service"))
                .route("possupplier-service", r -> r.path("/auth/**", "/api/supplier/**")
                        .filters(f -> f.filter(filter))
                        .uri("lb://possupplier-service"))
                .route("productsales-service", r -> r.path("/auth/**", "/api/dashboard/**", "/api/store/**") // Add /api/metrics/** here
                        .filters(f -> f.filter(filter))
                        .uri("lb://productsales-service"))
                .build();
    }
}