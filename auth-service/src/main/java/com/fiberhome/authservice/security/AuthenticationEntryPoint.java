package com.fiberhome.authservice.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.transform.Result;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Component
public class AuthenticationEntryPoint implements org.springframework.security.web.AuthenticationEntryPoint {

    private static final Logger logger = LoggerFactory.getLogger(AuthenticationEntryPoint.class);

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException)
      throws IOException, ServletException {
            logger.error("Unauthorized error: {}", authException.getMessage());

            response.setContentType(MediaType.APPLICATION_JSON_VALUE);


            final Map<String, Object> body = new HashMap<>();
            if(authException instanceof BadCredentialsException) {
                Result invalidUserNameOrPassword =
                        (Result) Collections.singletonMap("101","Incorrect username or password");
                body.put("status", invalidUserNameOrPassword);
                body.put("error", "Invalid Credentials");
            } else {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED,
                        "Unauthorized access");
                body.put("error", "Unauthorized");
            }
            body.put("message", authException.getMessage());
            body.put("path", request.getServletPath());

            final ObjectMapper mapper = new ObjectMapper();
            mapper.writeValue(response.getOutputStream(), body);
    }
}
