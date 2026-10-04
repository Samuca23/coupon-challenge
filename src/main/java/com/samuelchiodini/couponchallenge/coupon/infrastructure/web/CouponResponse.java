package com.samuelchiodini.couponchallenge.coupon.infrastructure.web;

import com.samuelchiodini.couponchallenge.coupon.domain.Coupon;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record CouponResponse(
        UUID id,
        String code,
        String description,
        BigDecimal discountValue,
        Instant expirationDate,
        String status,
        boolean published,
        boolean redeemed) {

    public static CouponResponse from(Coupon coupon) {
        return new CouponResponse(
                coupon.getId(),
                coupon.getCode().value(),
                coupon.getDescription(),
                coupon.getDiscountValue().value(),
                coupon.getExpirationDate(),
                coupon.getStatus().name(),
                coupon.isPublished(),
                coupon.isRedeemed());
    }
}
