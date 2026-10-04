package com.samuelchiodini.couponchallenge.coupon.domain.exceptions;

public class InvalidExpirationDateException extends RuntimeException {

    public InvalidExpirationDateException(String message) {
        super(message);
    }
}
