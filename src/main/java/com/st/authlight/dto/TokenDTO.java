package com.st.authlight.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.st.authlight.service.jwt.JwtService;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.time.Instant;
import java.util.Date;

@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TokenDTO {

    private String accessToken;
    private String refreshToken;
    private Instant expiredAt;

    private Boolean passwordExpired = null;

    public TokenDTO(String accessToken, String refreshToken, Date expiredAt) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        if (expiredAt != null)
            this.expiredAt = expiredAt.toInstant();
    }

    public static TokenDTO createPasswordExpired() {
        TokenDTO token = new TokenDTO();
        token.setPasswordExpired(true);
        token.setAccessToken(null);
        token.setRefreshToken(null);
        token.setExpiredAt(null);
        return token;
    }
}