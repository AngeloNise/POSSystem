package com.gateway.edge.service.security;

import com.auth0.jwt.interfaces.Claim;
import com.gateway.edge.service.security.util.JwtTokenUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Map;

@RefreshScope
@Component
@Slf4j
public class AuthenticationFilter implements GatewayFilter {

    @Autowired
    private RouterValidator routerValidator;
    @Autowired
    private JwtTokenUtils jwtTokenUtils;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();

        if (routerValidator.isSecured.test(request)) {
            if(this.isAuthMissing(request)){
                return this.onError(exchange, "Authorization header is missing in request", HttpStatus.UNAUTHORIZED);
            }
            final String token = StringUtils.substringAfter(this.getAuthHeader(request)," ");
            MultiValueMap<String, HttpCookie> cookie = request.getCookies();
            /*String loggedInCookie = StringUtils.defaultIfBlank(cookie.getFirst("logged_in")
                    .getValue(),null);*/
            if (!cookie.containsKey("logged_in") || jwtTokenUtils.isInvalid(token)) {
                //try refresh
                // Retrieve the cookies from the request
                // Do all of the validation on the cookie.
                if(!cookie.containsKey("refreshToken")){
                    return this.onError(exchange, "Token expired kindly re-login again.", HttpStatus.UNAUTHORIZED);
                }
                String refreshToken = StringUtils.defaultIfBlank(cookie.getFirst("refreshToken")
                        .getValue(),null);

                try {
                    String encryptedToken = jwtTokenUtils.validateCookie(refreshToken);
                    //if valid and not expired mutate request with new token
                    if(StringUtils.isNotBlank(encryptedToken)){
                        exchange.getRequest().mutate()
                                .header(HttpHeaders.AUTHORIZATION, "Bearer "+ encryptedToken)
                                .build();
                        exchange.getResponse().addCookie(generateLoggedInCookie());
                    }else{
                        return this.onError(exchange, "Token expired kindly re-login again.", HttpStatus.UNAUTHORIZED);
                    }
                } catch (Exception e) {
                    return this.onError(exchange, "Token expired kindly re-login again.", HttpStatus.UNAUTHORIZED);
                }
            }
        }
        return chain.filter(exchange);
    }

    public ResponseCookie generateLoggedInCookie(){
        return
                ResponseCookie.from("logged_in", String.valueOf(true))
                //.path("/insys/")
                .path("/")
                //.secure(true)
                //.domain("209.146.26.68")
                .maxAge(Duration.parse("P1D"))
                //.sameSite("Lax")
               .build();
    }

    private Mono<Void> onError(ServerWebExchange exchange, String err, HttpStatus httpStatus) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(httpStatus);
        return response.setComplete();
    }

    private String getAuthHeader(ServerHttpRequest request) {
        return request.getHeaders().getOrEmpty("Authorization").get(0);
    }

    private boolean isAuthMissing(ServerHttpRequest request) {

        return (!request.getHeaders().containsKey("Authorization") && !request.getHeaders().containsKey("authorization"));

    }

    private void populateRequestWithHeaders(ServerWebExchange exchange, String token) {

        Map<String, Claim> claims = jwtTokenUtils.getAllClaimsFromToken(token);
        exchange.getRequest().mutate()
                .header("id", String.valueOf(claims.get("id")))
                .header("role", String.valueOf(claims.get("authorities")))
                .build();
    }
}
