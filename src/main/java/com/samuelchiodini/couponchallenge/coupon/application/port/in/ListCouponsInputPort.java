package com.samuelchiodini.couponchallenge.coupon.application.port.in;

import com.samuelchiodini.couponchallenge.coupon.domain.Coupon;

import java.util.List;

public interface ListCouponsInputPort {

    List<Coupon> execute();
}
