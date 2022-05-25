package com.st.authlight.config.properties;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;
import java.util.Optional;

@ConfigurationProperties("auth")
public record AuthClientsProperties(List<Client> clients) {

    public boolean hasClient(String clientId, String clientSecret) {
        return clients.contains(new Client(clientId, clientSecret));
    }

    public Optional<Cookie> getSecretCookie(String clientId, String clientSecret) {
        var client = new Client(clientId, clientSecret);

        return clients.stream()
                .filter(client::equals)
                .findFirst()
                .map(Client::getCookie);
    }

    @Data
    @NoArgsConstructor
    @ConfigurationProperties("auth.clients")
    public static class Client {

        private String clientId;
        private String clientSecret;

        @EqualsAndHashCode.Exclude
        private Cookie cookie = null;

        public Client(String clientId, String clientSecret) {
            this.clientId = clientId;
            this.clientSecret = clientSecret;
        }
    }

    @ConfigurationProperties("auth.clients.cookie")
    public record Cookie(String name, String value) {
    }
}