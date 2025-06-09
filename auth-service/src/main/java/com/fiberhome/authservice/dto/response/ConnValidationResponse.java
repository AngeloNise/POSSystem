package com.fiberhome.authservice.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import org.springframework.security.core.GrantedAuthority;

import java.util.List;

@Getter
@Builder
@ToString
public class ConnValidationResponse {
    private String status;
    private boolean isAuthenticated;
    private String methodType;
    private String username;
    private List<GrantedAuthority> authorities;
}
