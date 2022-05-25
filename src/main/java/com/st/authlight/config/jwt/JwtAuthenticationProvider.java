package com.st.authlight.config.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;

import java.util.List;

import static org.apache.commons.lang3.StringUtils.EMPTY;

@Service
public class JwtAuthenticationProvider {

    private static final String ROLE_PREFIX = "ROLE_";
    private static final String AUTHORITIES_KEY = "role";

    private final JwtParser jwtParser;

    public JwtAuthenticationProvider(@Value("${jwt.secret.access}") String secret) {
        jwtParser = Jwts.parser().setSigningKey(secret);
    }

    public Authentication getAuthentication(String jwt) {
        var claims = jwtParser.parseClaimsJws(jwt).getBody();
        var authorities = getGrantedAuthorities(claims);
        var principal = new User(claims.getSubject(), EMPTY, authorities);

        return new UsernamePasswordAuthenticationToken(principal, jwt, authorities);
    }

    public boolean isValid(String jwt) {
        try {
            jwtParser.parseClaimsJws(jwt);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private List<GrantedAuthority> getGrantedAuthorities(Claims claims) {
        if (!claims.containsKey(AUTHORITIES_KEY)) return List.of();
        return List.of(new SimpleGrantedAuthority(ROLE_PREFIX + claims.get(AUTHORITIES_KEY).toString()));
    }
}