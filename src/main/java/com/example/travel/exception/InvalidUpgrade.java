package com.example.travel.exception;

public class InvalidUpgrade extends RuntimeException {
    public InvalidUpgrade(String message) {
        super(message);
    }
}
