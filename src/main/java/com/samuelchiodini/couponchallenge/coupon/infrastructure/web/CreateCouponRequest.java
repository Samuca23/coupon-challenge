package com.samuelchiodini.couponchallenge.coupon.infrastructure.web;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.Instant;

public record CreateCouponRequest(
        @NotBlank String code,
        @NotBlank String description,
        @NotNull BigDecimal discountValue,
        @NotNull Instant expirationDate,
        Boolean published) {
}
