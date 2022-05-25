package com.st.authlight.util;

import org.springframework.http.ResponseCookie;

import static java.util.concurrent.TimeUnit.MINUTES;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.springframework.boot.web.server.Cookie.SameSite.NONE;

public class CookieUtil {

    public static ResponseCookie createCookie(String name, String value, Long ttl) {
        return ResponseCookie.from(name, value)
                .httpOnly(true)
                .path("/")
                .maxAge(MINUTES.toSeconds(ttl))
                .secure(true)
                .build();
    }

    public static ResponseCookie createEmptyCookie(String name) {
        return ResponseCookie.from(name, EMPTY)
                .httpOnly(true)
                .path("/")
                .maxAge(0)
                .secure(true)
                .build();
    }
}