package com.st.authlight.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("jwt")
@Data
public class JwtProperties {
   public record Ttl(Long accessTtl, Long refreshTtl){}
    public record Secret(String access, String refresh){}

    private Ttl ttl;
    private Secret secret;
}
