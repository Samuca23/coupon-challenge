package com.samuelchiodini.couponchallenge.coupon.domain.exceptions;

import java.util.UUID;

public class CouponAlreadyDeletedException extends RuntimeException {

    public CouponAlreadyDeletedException(UUID couponId) {
        super("cupom " + couponId + " já está deletado");
    }
}
