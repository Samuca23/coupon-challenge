package com.samuelchiodini.couponchallenge.coupon.domain.exceptions;

public class InvalidCouponCodeException extends RuntimeException {

    public InvalidCouponCodeException(String message) {
        super(message);
    }
}
