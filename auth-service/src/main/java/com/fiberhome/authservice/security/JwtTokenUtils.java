package com.fiberhome.authservice.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fiberhome.authservice.model.LoginUserDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.web.util.WebUtils;

import javax.annotation.PostConstruct;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@Slf4j
@RequiredArgsConstructor
public class JwtTokenUtils implements Serializable {

    @Value("${app.jwtSecret}")
    private String jwtSecret;
    // @Value("${server.servlet.session.cookie.name}")
    //        private String jSessionId;
    private JWTVerifier verifier;
    private Algorithm algorithm;
    @PostConstruct
    public void init(){
        this.algorithm = Algorithm.HMAC256(jwtSecret.getBytes());
        this.verifier = JWT.require(algorithm).build();
    }
    public boolean validateJwtToken(String authToken) {
        try {
            DecodedJWT decodedJWT  = verifier.verify(authToken);
            //Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(authToken);
            return true;
        } catch (IllegalArgumentException e) {
            log.error("JWT claims string is empty: {}", e.getMessage());
        } catch(Exception e){
            log.error(StringUtils.join(e.getCause()," : ",e.getMessage()));
        }

        return false;
    }

    public Map<String, Object> generateJwtToken(Authentication authentication) throws Exception {

        LoginUserDetails userPrincipal = (LoginUserDetails) authentication.getPrincipal();

        List<String> authorities = userPrincipal.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority).collect(Collectors.toList());
        String accessJwt = JWT.create()
                .withSubject(userPrincipal.getUsername())
                .withClaim("authorities",
                        authorities)
                .withIssuedAt(new Date())
                .withIssuer("Fiberhome")
                //.withExpiresAt(Date.from(LocalDateTime.now().plusMinutes(3).toInstant(ZoneOffset.of("Asia/Singapore"))))
                .withExpiresAt(Date.from(LocalDateTime.now().plusDays(1)
                        .toInstant(ZoneId.of("Asia/Singapore").getRules().getOffset(
                                Instant.now())))
                )
                .withJWTId(String.valueOf(UUID.randomUUID()))
                .sign(algorithm);

        String accessJti = JWT.decode(accessJwt).getClaim("jti").asString();

        String refreshJwt = JWT.create().withSubject(userPrincipal.getUsername())
                .withClaim("authorities",authorities).withIssuedAt(new Date()).withIssuer("Fiberhome")
                //.withExpiresAt(Date.from(LocalDateTime.now().plusMinutes(10)
                .withExpiresAt(Date.from(LocalDateTime.now().plusDays(1)
                        .toInstant(ZoneId.of("Asia/Singapore").getRules().getOffset(
                                Instant.now())))
                )
                .withJWTId(accessJti)
                .sign(algorithm);

        String encodedJwt = new SecurityCipher().encrypt1(refreshJwt,jwtSecret);
        ResponseCookie cookieJwt = generateJwtCookie(encodedJwt);
        ResponseCookie loggedIn = generateLoggedInCookie();
        return Map.of("access",accessJwt,"cookie",cookieJwt,"logged_in",loggedIn,"jti",accessJti);
    }

    public String getJwtFromCookies(HttpServletRequest request) {
        Cookie cookie = WebUtils.getCookie(request, "refreshToken");
        if (cookie != null) {
            return cookie.getValue();
        } else {
            return null;
        }
    }
    public ResponseCookie generateJwtCookie(String token) {
        return ResponseCookie.from("refreshToken", token)
                //.path("/insys/")
                .path("/")
                //.secure(true)
                //.domain("209.146.26.68")
                //.sameSite("Lax")
                .maxAge(Duration.parse("P1D")).httpOnly(true).build();
    }

    public ResponseCookie generateLoggedInCookie(){
         return ResponseCookie.from("logged_in", String.valueOf(true))
                 //.path("/insys/")
                 .path("/")
                 //.secure(true)
                 //.domain("209.146.26.68")
                 .maxAge(Duration.parse("P1D"))
                 //.sameSite("Lax")
                 .build();
    }
    public ResponseCookie getCleanJwtCookie() {
        return ResponseCookie.from(jwtSecret, null).path("/api").build();
        /*return ResponseCookie.from("refreshToken", "")
                //.path("/insys")
                .path("/")
                .build();*/
    }
}
