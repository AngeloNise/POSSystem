package com.fiberhome.authservice.security.filters;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fiberhome.authservice.dto.response.JwtResponse;
import com.fiberhome.authservice.model.Account;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHeaders;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.stream.Collectors;
//import java.util.UUID;

@Slf4j
public class JWTAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
    private final AuthenticationManager authenticationManager;
    private ObjectMapper mapper=new ObjectMapper();

    public JWTAuthenticationFilter(AuthenticationManager authenticationmanager){
        this.authenticationManager = authenticationmanager;
    }

    @Value("${app.jwtSecret}")
    private String jwtSecret;

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        // LoginUserDetails authModel = mapper.readValue(request.getInputStream(), LoginUserDetails.class);
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        //Authentication authentication = new UsernamePasswordAuthenticationToken(authModel.getUsername(), authModel.getPassword());
        Authentication authentication = new UsernamePasswordAuthenticationToken(username,password);
        return authenticationManager.authenticate(authentication);

    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        Account acct = (Account)authResult.getPrincipal();

        String accessToken = JWT.create()
                .withSubject(authResult.getName())
                .withClaim("authorities",
                                authResult.getAuthorities().stream()
                                        .map(GrantedAuthority::getAuthority).collect(Collectors.toList()))
                .withIssuedAt(new Date())
                .withIssuer(request.getRequestURI().toString())
                .withExpiresAt(Date.from(LocalDateTime.now().plusMinutes(30).toInstant(ZoneOffset.UTC)))
                .sign(Algorithm.HMAC256(jwtSecret.getBytes()));

        String refreshToken = JWT.create()
                        .withSubject(authResult.getName())
                        .withClaim("authorities",
                                authResult.getAuthorities().stream()
                                        .map(GrantedAuthority::getAuthority).collect(Collectors.toList()))
                        .withIssuedAt(new Date())
                        .withIssuer(request.getRequestURI().toString())
                        .withExpiresAt(Date.from(LocalDateTime.now().plusMinutes(30).toInstant(ZoneOffset.UTC)))
                        .sign(Algorithm.HMAC256(jwtSecret.getBytes()));

        log.info(accessToken);

      /*  TokensEntity tokensEntity = TokensEntity.builder().id(String.valueOf(UUID.randomUUID())).authenticationToken(token)
                .username(authResult.getName())
                .createdBy("SYSTEM").createdOn(LocalDateTime.now())
                .modifiedBy("SYSTEM").modifiedOn(LocalDateTime.now())
                .build();

        tokensEntity = tokensRedisService.save(tokensEntity); */
        //response.addHeader("Authorization ", String.format("Bearer %s", tokensEntity.getId()));
        //response.addHeader("Authorization", String.format("Bearer %s", accessToken));
        //response.addHeader("Expiration", String.valueOf(30*60));

        //ConnValidationResponse respModel = ConnValidationResponse.builder().isAuthenticated(true).build();

        JwtResponse respModel = JwtResponse.builder()
                .username(authResult.getName())
                //.accessToken(accessToken)
                //.refreshToken(refreshToken)
                .roles(authResult.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority)
                        .collect(Collectors.toList()))
                .isAuthenticated(true)
                .build();

        response.addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        response.getOutputStream().write(mapper.writeValueAsBytes(respModel));
    }


}
