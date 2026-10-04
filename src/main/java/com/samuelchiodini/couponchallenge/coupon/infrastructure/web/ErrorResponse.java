package com.samuelchiodini.couponchallenge.coupon.infrastructure.web;

import java.util.List;

public record ErrorResponse(String error, String message, List<String> fields) {

    public static ErrorResponse of(String error, String message) {
        return new ErrorResponse(error, message, null);
    }

    public static ErrorResponse of(String error, String message, List<String> fields) {
        return new ErrorResponse(error, message, fields);
    }
}
