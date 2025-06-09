package com.gateway.edge.service.security.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

@Component
@Slf4j
public class JwtTokenUtils {

    @Value("${app.jwtSecret}")
    private String jwtSecret; //= "iworks#2022fh@";
    JWTVerifier verifier;
    Algorithm algorithm;

    @PostConstruct
    public void init(){
        this.algorithm = Algorithm.HMAC256(jwtSecret.getBytes());
        this.verifier = JWT.require(algorithm).build();
    }

    private boolean isTokenExpired(String token) {
        return this.getExpiryToken(token).before(new Date());
    }

    public Map<String, Claim> getAllClaimsFromToken(String token) {
        DecodedJWT jwt = verifier.verify(token);

        return jwt.getClaims();
    }

    public Date getExpiryToken(String token) {
        //return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
        DecodedJWT jwt;
        try{
             jwt = verifier.verify(token);
        }catch(TokenExpiredException te){
            return Date.from(LocalDateTime.now().minusDays(1)
                    .toInstant(ZoneId.of("Asia/Singapore").getRules().getOffset(
                            Instant.now())));
        }
        return jwt.getExpiresAt();
    }

    public boolean isInvalid(String token) {
        return this.isTokenExpired(token);
    }

    public String validateCookie(String refreshToken)
            throws Exception {

        SecurityCipher securityCipher = new SecurityCipher();
        String newCookie = "";
        String decodedJwt = securityCipher.decrypt1(refreshToken,jwtSecret);
        try{
            DecodedJWT jwt = verifier.verify(decodedJwt);
            if(jwt.getExpiresAt().after(new Date())){
                //newCookie = securityCipher.encrypt1(decodedJwt,jwtSecret);
                //log.info(String.valueOf(jwt.getClaim("authorities").asList(String.class)));
                newCookie =  JWT.create()
                        .withSubject(jwt.getSubject())
                        .withClaim("authorities",
                                jwt.getClaim("authorities").asList(String.class))
                        .withIssuedAt(new Date())
                        .withIssuer(jwt.getIssuer())
                        //.withExpiresAt(Date.from(LocalDateTime.now().plusMinutes(3).toInstant(ZoneOffset.of("Asia/Singapore"))))
                        .withExpiresAt(Date.from(LocalDateTime.now().plusDays(1)
                                .toInstant(ZoneId.of("Asia/Singapore").getRules().getOffset(
                                        Instant.now())))
                        )
                        .withJWTId(jwt.getId())
                        .sign(algorithm);
            }
        } catch (IllegalArgumentException ex) {
            log.error("JWT claims string is empty.");
            throw new IllegalArgumentException("JWT claims string is empty.");
        }
        return newCookie;
    }

}
