package com.st.authlight.service;

import com.st.authlight.config.properties.JwtProperties;
import com.st.authlight.dto.TokenDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Service;

import static com.st.authlight.util.CookieUtil.createCookie;
import static com.st.authlight.util.CookieUtil.createEmptyCookie;

@Slf4j
@Service
@RequiredArgsConstructor
public class CookieService {

    private final JwtProperties jwtProperties;

    public void addTokens(TokenDTO dto, ServerHttpResponse response) {
        log.debug("Setting cookie for tokens: {}", dto);

        try {
            var accessTokenCookie = createCookie("AccessToken", dto.getAccessToken(), jwtProperties.getTtl().accessTtl());
            var refreshTokenCookie = createCookie("RefreshToken", dto.getRefreshToken(), jwtProperties.getTtl().refreshTtl());

            response.addCookie(accessTokenCookie);
            response.addCookie(refreshTokenCookie);

        } catch (Exception ex) {
            log.error("Failed to set cookie for tokens: {}", dto, ex);
        }
    }


    public void removeTokens(ServerHttpResponse response) {
        log.debug("Starting to remove cookie from response");

        try {
            var accessTokenCookie = createEmptyCookie("AccessToken");
            var refreshTokenCookie = createEmptyCookie("RefreshToken");

            response.addCookie(accessTokenCookie);
            response.addCookie(refreshTokenCookie);

            log.debug("Finished to remove cookie from response");
        } catch (Exception ex) {
            log.error("Failed to remove cookie from response");
        }
    }
}