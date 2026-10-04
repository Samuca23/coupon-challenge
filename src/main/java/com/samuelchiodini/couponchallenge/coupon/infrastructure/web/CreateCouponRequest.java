package com.samuelchiodini.couponchallenge.coupon.infrastructure.web;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.Instant;

public record CreateCouponRequest(
        @NotBlank String code,
        @NotBlank String description,
        @NotNull BigDecimal discountValue,
        @NotNull
        @Schema(description = "Data de expiração em ISO-8601. Milissegundos são opcionais.",
                example = "2027-01-01T10:00:00Z")
        Instant expirationDate,
        Boolean published) {
}
