package com.samuelchiodini.couponchallenge.coupon.domain.exceptions;

public class InvalidDiscountValueException extends RuntimeException {

    public InvalidDiscountValueException(String message) {
        super(message);
    }
}
