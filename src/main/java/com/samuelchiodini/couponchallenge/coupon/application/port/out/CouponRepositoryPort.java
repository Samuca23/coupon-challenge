package com.samuelchiodini.couponchallenge.coupon.application.port.out;

import com.samuelchiodini.couponchallenge.coupon.domain.Coupon;

import java.util.Optional;
import java.util.UUID;

public interface CouponRepositoryPort {

    Coupon save(Coupon coupon);

    Optional<Coupon> findById(UUID id);
}
