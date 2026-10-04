package com.samuelchiodini.couponchallenge.coupon.application.port.in;

import com.samuelchiodini.couponchallenge.coupon.application.usecase.CreateCouponCommand;
import com.samuelchiodini.couponchallenge.coupon.domain.Coupon;

public interface CreateCouponInputPort {

    Coupon execute(CreateCouponCommand command);
}
