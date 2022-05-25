package com.st.authlight.config.jwt;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public final class AuthenticationManager implements ReactiveAuthenticationManager {

    private final JwtAuthenticationProvider authenticationProvider;

    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {
        return Mono.just(authentication.getCredentials().toString())
                .filter(authenticationProvider::isValid)
                .map(authenticationProvider::getAuthentication);
    }
}