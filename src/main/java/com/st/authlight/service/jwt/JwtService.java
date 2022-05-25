package com.st.authlight.service.jwt;

import com.st.authlight.config.properties.JwtProperties;
import com.st.authlight.dto.TokenDTO;
import com.st.authlight.entity.User;
import com.st.authlight.exception.NoTokenException;
import com.st.authlight.util.DateUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureException;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Optional;

import static io.jsonwebtoken.Claims.EXPIRATION;
import static io.jsonwebtoken.Claims.SUBJECT;
import static io.jsonwebtoken.SignatureAlgorithm.HS512;
import static java.lang.Long.parseLong;
import static java.lang.String.valueOf;
import static java.time.Instant.now;

@Slf4j
@Service
@RequiredArgsConstructor
public class JwtService {

    public static final String ADMINISTRATOR = "ADMINISTRATOR";
    private final JwtProperties jwtProperties;
    public static final String CLAIMS_ROLE = "role";
    public static final String ACCESS_TOKEN = "AccessToken";
    public static final String AUTHORIZATION_HEADER = "authorization";

    private static final String BEARER_PREFIX = "Bearer ";


    public TokenDTO generate(String token) {
        var user = parseAccess(token);
        return generate(user);
    }

    public TokenDTO generate(User user) {
        var role = user.getRole();
        var expiration = getAccessExpiration();
        var accessToken = getAccessToken(user, expiration);
        var refreshToken = getRefreshToken(user, role);

        return user.getPasswordExpired() != null && user.getPasswordExpired()
                ? TokenDTO.createPasswordExpired()
                : new TokenDTO(accessToken, refreshToken, expiration);
    }

    public Optional<String> getAccessTokenFromHeaderCookies(ServerHttpRequest request) {
        var authHeader = request.getHeaders().getFirst(AUTHORIZATION_HEADER);
        if (!StringUtils.isEmpty(authHeader)) {
            log.debug("Found auth header: {}", authHeader);
            return Optional.of(authHeader.substring(BEARER_PREFIX.length()));
        }

        var cookies = request.getCookies();
        var tokenCookies = cookies.getFirst(ACCESS_TOKEN);
        if (tokenCookies == null) return Optional.empty();
        log.debug("Found auth cookie: {}", tokenCookies);

        return Optional.of(tokenCookies.getValue());
    }

    public boolean shouldRefreshToken(String token) {
        var claims = getAllClaimsFromToken(token);
        var expirationDateInSeconds = getExpirationForCurrentUserRoleInSeconds(claims);
        var shouldRefresh = isShouldRefreshToken(expirationDateInSeconds);

        var role = claims.get(CLAIMS_ROLE);
        var subject = claims.get(SUBJECT);
        log.debug("Checked token for expiration for role: {} and userId: {}. shouldRefreshToken: {}", role, subject, shouldRefresh);

        return shouldRefresh;
    }

    private Claims getAllClaimsFromToken(String token)
            throws SignatureException, ExpiredJwtException, IllegalArgumentException {
        try {
            return Jwts.parser()
                    .setSigningKey(jwtProperties.getSecret().access())
                    .parseClaimsJws(token)
                    .getBody();
        } catch (IllegalArgumentException e) {
            throw new NoTokenException();
        }
    }

    private Long getExpirationForCurrentUserRoleInSeconds(Claims claimsFromToken) {
        var oTtl = claimsFromToken.get(EXPIRATION);
        if (oTtl == null) throw new NoTokenException();

        return Long.parseLong(oTtl.toString());
    }

    private boolean isShouldRefreshToken(long tokenExpirationDateInSeconds) {
        var currentDateSeconds = now().getEpochSecond();
        var ttlForTokenFromConfig = jwtProperties.getTtl().accessTtl();
        log.debug("Token will expire at: {}. Current date: {}. ttlForTokenFromConfig: {}", Instant.ofEpochSecond(tokenExpirationDateInSeconds), Instant.ofEpochSecond(currentDateSeconds), DateUtil.formatDuration(Duration.of(ttlForTokenFromConfig, ChronoUnit.SECONDS)));

        var remainingTokenTimeInSeconds = tokenExpirationDateInSeconds - currentDateSeconds;
        var shouldRefreshToken = remainingTokenTimeInSeconds < ttlForTokenFromConfig / 2;
        log.debug("Remaining token time: {}. Full ttl duration from config: {}. shouldRefreshToken: {}", DateUtil.formatDuration(Duration.of(remainingTokenTimeInSeconds, ChronoUnit.SECONDS)), DateUtil.formatDuration(Duration.of(ttlForTokenFromConfig, ChronoUnit.SECONDS)), shouldRefreshToken);

        return shouldRefreshToken;
    }

    private String getAccessToken(User user, Date expiration) {
        return getToken(user, expiration, jwtProperties.getSecret().access());
    }

    private String getRefreshToken(User user, String role) {
        return getToken(user, getRefreshExpiration(), jwtProperties.getSecret().refresh());
    }

    private String getToken(User user, Date expiration, String secret) {
        return Jwts.builder()
                .setSubject(user.getId().toString())
                .claim(CLAIMS_ROLE, user.getRole())
                .setIssuedAt(new Date())
                .setExpiration(expiration)
                .signWith(HS512, secret)
                .compact();
    }

    private Date getAccessExpiration() {
        return Date.from(Instant.ofEpochSecond(now().getEpochSecond() + jwtProperties.getTtl().accessTtl()));
    }

    private Date getRefreshExpiration() {
        return Date.from(Instant.ofEpochSecond(now().getEpochSecond() + jwtProperties.getTtl().refreshTtl()));
    }


    public User parseRefresh(String token) {
        var claims = Jwts.parser().setSigningKey(jwtProperties.getSecret().refresh()).parseClaimsJws(token).getBody();
        var userId = Long.valueOf(claims.getSubject());
        var role = claims.get("role", String.class);

        var user = new User();
        user.setId(userId);
        user.setRole(role);

        return user;
    }

    public User parseAccess(String token) {
        var claims = Jwts.parser().setSigningKey(jwtProperties.getSecret().access()).parseClaimsJws(token).getBody();
        var userId = Long.valueOf(claims.getSubject());
        var role = claims.get("role", String.class);

        var user = new User();
        user.setId(userId);
        user.setRole(role);

        return user;
    }

    private Long toLong(Object o) {
        Long value = null;
        if (o != null) {
            value = parseLong(o.toString());
        }
        return value;
    }

    @Data
    @AllArgsConstructor
    public static class Token {
        private Date expiration;
        private String access;
        private String refresh;
    }
}