package com.fiberhome.authservice.controller;

import com.code.share.codesharing.model.MessageResponse;
import com.fiberhome.authservice.dto.requests.LoginRequest;
import com.fiberhome.authservice.dto.response.JwtResponse;
import com.fiberhome.authservice.dto.response.OrganizationResponseDTO;
import com.fiberhome.authservice.exceptions.AuthenticationException;
import com.fiberhome.authservice.model.LoginUserDetails;
import com.fiberhome.authservice.security.JwtTokenUtils;
import com.fiberhome.authservice.service.AuthenticationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {
    private final AuthenticationManager authenticationManager;

    private final AuthenticationService authService;

    private JwtTokenUtils jwtUtils;

    @ExceptionHandler({ AuthenticationException.class })
    public ResponseEntity<String> handleAuthenticationException(AuthenticationException e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
    }

    @GetMapping("/show")
    public String show(){

        return "Authentication Service is ONLINE";
    }

    @PostMapping(value = "/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) throws Exception {
        Authentication authentication;
        try {
            authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));
            if (authentication.isAuthenticated()) {
                SecurityContextHolder.getContext().setAuthentication(authentication);
                LoginUserDetails authUser =  (LoginUserDetails) authentication.getPrincipal();
                List<String> roles = authUser.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority).collect(Collectors.toList());

                Map<String,Object> auth = authService.handleAuthenticationObjects(authentication,authUser,roles);
                Map<String,Object> jwt = (Map<String, Object>) MapUtils.getMap(auth,"jwt");

                return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, MapUtils.getString(jwt,"cookie"))
                        .header(HttpHeaders.SET_COOKIE, MapUtils.getString(jwt,"logged_in"))
                        .body(JwtResponse.builder()
                                .id(MapUtils.getString(jwt,"access"))
                                .username(authUser.getUsername())
                                .organizationTree((List<OrganizationResponseDTO> )MapUtils.getObject(auth,"org"))
                                .organizationLedgerTree((List<OrganizationResponseDTO>)MapUtils.getObject(auth, "orgLedger"))
                                .organizationLedgerAreaTree((List<OrganizationResponseDTO>)MapUtils.getObject(auth, "orgLedgerArea"))
                                .userDetailsLog((Map)MapUtils.getObject(auth, "userDetails"))
                                .organizationGrade1((List<Map>)MapUtils.getObject(auth, "organizationGrade1"))
                                .organizationGrade2((List<Map>)MapUtils.getObject(auth, "organizationGrade2"))
                                .organizationGrade3((List<Map>)MapUtils.getObject(auth, "organizationGrade3"))
                                .organizationGrade4((List<Map>)MapUtils.getObject(auth, "organizationGrade4"))
                                .assignedOrganizationId(authUser.getOrganizationId())
                                .roles(roles)
                                .isAuthenticated(true).build());
            }
        } catch (DisabledException e) {
            throw new AuthenticationException("USER_DISABLED", e);
        } catch (BadCredentialsException e) {
            throw new AuthenticationException("INVALID_CREDENTIALS", e);
        }
        return null;
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logoutUser() {
        ResponseCookie cookie = jwtUtils.getCleanJwtCookie();
        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(new MessageResponse("You've been signed out!"));
    }

    /*@GetMapping("/refreshToken")
    public void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String authorizationHeader = request.getHeader(AUTHORIZATION);
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            try {
                String refreshToken = StringUtils.substring(authorizationHeader, "Bearer ".length());
                Algorithm algorithm = Algorithm.HMAC256(jwtSecret.getBytes());
                JWTVerifier verifier = JWT.require(algorithm).build();
                DecodedJWT decodedJWT = verifier.verify(refreshToken);

                String username = decodedJWT.getSubject();
                Account acct = loginUserDetailsService.getAccount(username);
                String accessToken = JWT.create()
                        .withSubject(acct.getUsername())
                        .withClaim("authorities",
                                acct.getRoles().stream().map(Role::getRoleName).collect(Collectors.toList())
                        )
                        .withIssuer(request.getRequestURI())
                        .withExpiresAt(Date.from(LocalDateTime.now().plusMinutes(30).toInstant(ZoneOffset.UTC)))
                        .sign(Algorithm.HMAC256(jwtSecret.getBytes()));
             *//* String[] roles = decodedJWT.getClaim("roles").asArray(String.class);
              Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
              stream(roles).forEach(role -> authorities.add(new SimpleGrantedAuthority(role)));

              UsernamePasswordAuthenticationToken authenticationToken =
                      new UsernamePasswordAuthenticationToken(username,null, authorities);
              SecurityContextHolder.getContext().setAuthentication(authenticationToken); *//*
                Map<String, String> tokens = new HashMap<>();
                tokens.put("access_token", accessToken);
                tokens.put("refresh_token", refreshToken);
                response.setContentType(APPLICATION_JSON_VALUE);
                new ObjectMapper().writeValue(response.getOutputStream(), tokens);
            } catch (Exception e) {
                response.setHeader("Error", e.getMessage());
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                Map<String, String> error = new HashMap<>();
                error.put("error_message", e.getMessage());
                response.setContentType(APPLICATION_JSON_VALUE);
                new ObjectMapper().writeValue(response.getOutputStream(), error);
            }
        }
    }*/


}