package com.samuelchiodini.couponchallenge.coupon.domain.exceptions;

import java.util.UUID;

public class CouponNotFoundException extends RuntimeException {

    public CouponNotFoundException(UUID couponId) {
        super("cupom " + couponId + " não encontrado");
    }
}
