package com.st.authlight.config.jwt;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpCookie;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.web.server.context.ServerSecurityContextRepository;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@Slf4j
@Component
@RequiredArgsConstructor
public final class SecurityContextRepository implements ServerSecurityContextRepository {

    private static final String BEARER_PREFIX = "Bearer ";
    private static final String ACCESS_TOKEN = "AccessToken";

    private final AuthenticationManager authenticationManager;

    @Override
    public Mono<Void> save(ServerWebExchange swe, SecurityContext sc) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Mono<SecurityContext> load(ServerWebExchange swe) {
        var request = swe.getRequest();

        return getBearerToken(request).flatMap(token -> {
            var auth = new UsernamePasswordAuthenticationToken(token, token);
            return authenticationManager.authenticate(auth).map(SecurityContextImpl::new);
        });
    }

    private Mono<String> getCookieToken(ServerHttpRequest request) {
        return Mono.justOrEmpty(request.getCookies().getFirst(ACCESS_TOKEN))
                .map(HttpCookie::getValue);
    }

    private Mono<String> getBearerToken(ServerHttpRequest request) {
        return Mono.justOrEmpty(request.getHeaders().getFirst(AUTHORIZATION))
                .filter(authHeader -> authHeader.startsWith(BEARER_PREFIX))
                .map(authHeader -> authHeader.substring(BEARER_PREFIX.length()));
    }
}