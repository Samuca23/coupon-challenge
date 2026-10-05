package com.samuelchiodini.couponchallenge.coupon.domain;

import com.samuelchiodini.couponchallenge.coupon.domain.exceptions.InvalidCouponCodeException;

import java.util.regex.Pattern;

public final class CouponCode {

    private static final Pattern NON_ALPHANUMERIC = Pattern.compile("[^A-Za-z0-9]");
    private static final int REQUIRED_LENGTH = 6;

    private final String value;

    private CouponCode(String value) {
        this.value = value;
    }

    public static CouponCode of(String raw) {
        String normalized = NON_ALPHANUMERIC.matcher(raw == null ? "" : raw).replaceAll("");
        if (normalized.length() != REQUIRED_LENGTH) {
            throw new InvalidCouponCodeException(
                    "code deve resultar em exatamente " + REQUIRED_LENGTH + " caracteres alfanuméricos");
        }
        return new CouponCode(normalized);
    }

    public String value() {
        return value;
    }
}
