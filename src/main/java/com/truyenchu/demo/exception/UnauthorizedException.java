package com.truyenchu.demo.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import lombok.Getter;
import java.util.Map;

@ResponseStatus(HttpStatus.UNAUTHORIZED)
@Getter
public class UnauthorizedException extends RuntimeException {
    private final Map<String, Object> response;

    public UnauthorizedException(String message) {
        super(message);
        this.response = null;
    }

    public UnauthorizedException(Map<String, Object> response) {
        super((String) response.get("message"));
        this.response = response;
    }
} 