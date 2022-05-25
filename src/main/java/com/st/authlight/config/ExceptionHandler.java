package com.st.authlight.config;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.google.gson.Gson;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import static java.time.ZoneOffset.UTC;
import static java.util.Objects.isNull;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.MediaType.APPLICATION_JSON;

@Slf4j
@Order(-2)
@Configuration
@RequiredArgsConstructor
public class ExceptionHandler implements ErrorWebExceptionHandler {

    private final static String DATE_FORMAT_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSS+0000";

    private final Gson gson;

    @Override
    public Mono<Void> handle(ServerWebExchange serverWebExchange, Throwable ex) {
        var message = ex.getMessage();
        var status = INTERNAL_SERVER_ERROR;

        if (ex instanceof ResponseStatusException respEx) {
            status = respEx.getStatus();
            message = respEx.getReason();
        } else if (ex instanceof IOException) {
            status = BAD_REQUEST;
        }

        if(isNull(message)) message = status.getReasonPhrase();

        log.error(message, ex);

        var errorInfo = new ErrorInfo();
        errorInfo.setStatus(status.value());
        errorInfo.setError(message);
        errorInfo.setTimestamp(ZonedDateTime.now(UTC).format(DateTimeFormatter.ofPattern(DATE_FORMAT_PATTERN)));
        errorInfo.setMessage(message);

        var response = serverWebExchange.getResponse();
        response.setStatusCode(status);
        response.getHeaders().setContentType(APPLICATION_JSON);

        var body = response.bufferFactory().wrap(gson.toJson(errorInfo).getBytes());
        return response.writeWith(Mono.just(body));
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ErrorInfo {
        private String timestamp;
        private int status;
        private String error;
        private String message;
    }
}