package com.quickplate.exception;

import java.time.LocalDateTime;

public class ErrorResponse {
    private String error;
    private int status;
    private LocalDateTime timestamp = LocalDateTime.now();

    public ErrorResponse(String error, int status) {
        this.error = error;
        this.status = status;
    }
    public String getError() { return error; }
    public int getStatus() { return status; }
    public LocalDateTime getTimestamp() { return timestamp; }
}