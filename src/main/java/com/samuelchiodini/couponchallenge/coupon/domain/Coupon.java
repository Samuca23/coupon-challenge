package com.samuelchiodini.couponchallenge.coupon.domain;

import com.samuelchiodini.couponchallenge.coupon.domain.exceptions.InvalidExpirationDateException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.util.UUID;

public final class Coupon {

    private static final Logger log = LoggerFactory.getLogger(Coupon.class);

    private final UUID id;
    private final CouponCode code;
    private final String description;
    private final DiscountValue discountValue;
    private final Instant expirationDate;
    private final CouponStatus status;
    private final boolean published;
    private final boolean redeemed;

    private Coupon(UUID id, CouponCode code, String description, DiscountValue discountValue,
                    Instant expirationDate, CouponStatus status, boolean published, boolean redeemed) {
        this.id = id;
        this.code = code;
        this.description = description;
        this.discountValue = discountValue;
        this.expirationDate = expirationDate;
        this.status = status;
        this.published = published;
        this.redeemed = redeemed;
    }

    public static Coupon create(String rawCode, String description, BigDecimal rawDiscountValue,
                                 Instant expirationDate, Boolean published, Clock clock) {
        CouponCode code = CouponCode.of(rawCode);
        DiscountValue discountValue = DiscountValue.of(rawDiscountValue);

        if (expirationDate == null || !expirationDate.isAfter(clock.instant())) {
            throw new InvalidExpirationDateException("expirationDate deve ser uma data futura");
        }

        boolean resolvedPublished = published != null && published;

        Coupon coupon = new Coupon(UUID.randomUUID(), code, description, discountValue,
                expirationDate, CouponStatus.ACTIVE, resolvedPublished, false);

        log.info("coupon {} created at {}", coupon.id, clock.instant());

        return coupon;
    }

    public static Coupon reconstitute(UUID id, CouponCode code, String description, DiscountValue discountValue,
                                       Instant expirationDate, CouponStatus status, boolean published,
                                       boolean redeemed) {
        return new Coupon(id, code, description, discountValue, expirationDate, status, published, redeemed);
    }

    public UUID getId() {
        return id;
    }

    public CouponCode getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public DiscountValue getDiscountValue() {
        return discountValue;
    }

    public Instant getExpirationDate() {
        return expirationDate;
    }

    public CouponStatus getStatus() {
        return status;
    }

    public boolean isPublished() {
        return published;
    }

    public boolean isRedeemed() {
        return redeemed;
    }
}
