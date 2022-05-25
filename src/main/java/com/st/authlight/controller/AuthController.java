package com.st.authlight.controller;

import com.st.authlight.dto.*;
import com.st.authlight.exception.NoTokenException;
import com.st.authlight.service.CookieService;
import com.st.authlight.service.jwt.JwtService;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.bind.annotation.*;
import static org.springframework.http.HttpStatus.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final JwtService jwtService;
    private final CookieService cookieService;

    private static final String AUTH_EXPIRED = "AUTH_EXPIRED";
    private static final String NO_AUTH = "NO_AUTH";

    @PostMapping("/v2/authenticated")
    public TokenDTO autoRefreshToken(ServerHttpRequest request, ServerHttpResponse response) {
        var tokenOpt = jwtService.getAccessTokenFromHeaderCookies(request);
        if (tokenOpt.isEmpty()) throw new NoTokenException();

        var token = tokenOpt.get();

        var shouldRefreshToken = jwtService.shouldRefreshToken(token);

        TokenDTO tokenDTO;

        if (shouldRefreshToken) {
            tokenDTO = jwtService.generate(token);
            cookieService.addTokens(tokenDTO, response);
        } else {
            tokenDTO = new TokenDTO();
            tokenDTO.setAccessToken(token);
        }

        return tokenDTO;
    }

    @ExceptionHandler(value = {SignatureException.class, MalformedJwtException.class, ExpiredJwtException.class})
    public ResponseEntity<?> expiredTokenException(Exception ex) {
        log.debug(ex.getLocalizedMessage());
        return ResponseEntity.status(UNAUTHORIZED).body(getFormatMessage(AUTH_EXPIRED));
    }

    @ExceptionHandler(NoTokenException.class)
    public ResponseEntity<?> noTokenException(NoTokenException ex) {
        log.debug(ex.getLocalizedMessage());
        return ResponseEntity.status(UNAUTHORIZED).body(getFormatMessage(NO_AUTH));
    }

    private String getFormatMessage(String message) {
        return "{\"message\":\"" + message + "\"}";
    }

}