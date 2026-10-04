package com.samuelchiodini.couponchallenge.coupon.application.usecase;

import java.math.BigDecimal;
import java.time.Instant;

public record CreateCouponCommand(
        String rawCode,
        String description,
        BigDecimal discountValue,
        Instant expirationDate,
        Boolean published) {
}
