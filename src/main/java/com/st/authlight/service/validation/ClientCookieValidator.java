package com.st.authlight.service.validation;

import com.st.authlight.config.properties.AuthClientsProperties;
import com.st.authlight.dto.ClientDTO;
import com.st.authlight.exception.UnauthorizedException;
import com.st.authlight.exception.UnsupportedClientException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpCookie;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Service;

import static java.util.Objects.isNull;

@Slf4j
@Service
public record ClientCookieValidator(AuthClientsProperties properties) implements ClientValidator {

    @Override
    public void validate(ClientDTO dto, ServerHttpRequest request) {
        var clientId = dto.getClientId();

        log.debug("Starting to validation request with clientId: {}, DTO: {}", clientId, dto);

        try {
            tryToValidate(dto, request);

            log.debug("Finished to validation request with clientId: {}", clientId);
        } catch (UnsupportedClientException ex) {
            log.error("ST-ERROR. Failed to validation request with clientId: {}. Unsupported client", clientId);
        } catch (UnauthorizedException ex) {
            log.error("ST-ERROR. Failed to validation request with clientId: {}. Not found cookie for client", clientId);
        } catch (Exception ex) {
            log.error("ST-ERROR. Failed to validation request with clientId: {}", clientId);
        }
    }

    private void tryToValidate(ClientDTO dto, ServerHttpRequest request) {
        var clientId = dto.getClientId();
        var clientSecret = dto.getClientSecret();

        if (!properties.hasClient(clientId, clientSecret)) {
            throw new UnsupportedClientException("Unsupported client: %s".formatted(clientId));
        }

        var secretCookieOpt = properties.getSecretCookie(clientId, clientSecret);

        if (secretCookieOpt.isEmpty()) return;

        var secretCookie = secretCookieOpt.get();
        var currentCookie = request.getCookies().getFirst(secretCookie.name());

        if (isInvalidCookie(secretCookie, currentCookie)) {
            throw new UnauthorizedException("Failed to authorize user");
        }
    }

    private boolean isInvalidCookie(AuthClientsProperties.Cookie secretCookie, HttpCookie currentCookie) {
        return isNull(currentCookie) || !secretCookie.value().equals(currentCookie.getValue());
    }
}