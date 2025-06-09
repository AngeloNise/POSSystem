package com.gateway.edge.service.security;

import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.Predicate;
import java.util.regex.Pattern;

@Component
public class RouterValidator {

    public static final List<String> OPEN_API_ENDPOINTS = List.of("/auth/login","announcements/subscribe","announcements/notifications/[a-zA-Z\\d]+");

    public Predicate<ServerHttpRequest> isSecured =
            request -> OPEN_API_ENDPOINTS
                    .stream()
                    .noneMatch(uri -> request.getURI().getPath().contains(uri)
                    || Pattern.compile(uri)
                            .matcher(request.getURI().getPath()).results().findFirst().isPresent())
            ;

}
