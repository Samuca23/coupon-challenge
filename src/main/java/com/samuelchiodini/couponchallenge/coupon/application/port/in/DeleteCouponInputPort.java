package com.samuelchiodini.couponchallenge.coupon.application.port.in;

import java.util.UUID;

public interface DeleteCouponInputPort {

    void execute(UUID couponId);
}
