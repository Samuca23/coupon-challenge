package com.samuelchiodini.couponchallenge.coupon.application.port.out;

import com.samuelchiodini.couponchallenge.coupon.domain.Coupon;

public interface CouponRepositoryPort {

    Coupon save(Coupon coupon);
}
