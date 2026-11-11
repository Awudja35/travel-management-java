package com.example.travel.exception;

public class PaymentFailed extends RuntimeException {
    public PaymentFailed(String message) {
        super(message);
    }
}
