package com.example.travel.exception;

public class NoSeatsAvailable extends RuntimeException {
    public NoSeatsAvailable(String message) {
        super(message);
    }
}