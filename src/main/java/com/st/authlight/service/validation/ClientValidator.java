package com.st.authlight.service.validation;

import com.st.authlight.dto.ClientDTO;
import org.springframework.http.server.reactive.ServerHttpRequest;

public interface ClientValidator {

    void validate(ClientDTO dto, ServerHttpRequest request);
}