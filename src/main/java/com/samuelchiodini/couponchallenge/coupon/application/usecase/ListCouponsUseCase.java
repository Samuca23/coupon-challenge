package com.samuelchiodini.couponchallenge.coupon.application.usecase;

import com.samuelchiodini.couponchallenge.coupon.application.port.in.ListCouponsInputPort;
import com.samuelchiodini.couponchallenge.coupon.application.port.out.CouponRepositoryPort;
import com.samuelchiodini.couponchallenge.coupon.domain.Coupon;

import java.util.List;

public class ListCouponsUseCase implements ListCouponsInputPort {

    private final CouponRepositoryPort couponRepositoryPort;

    public ListCouponsUseCase(CouponRepositoryPort couponRepositoryPort) {
        this.couponRepositoryPort = couponRepositoryPort;
    }

    @Override
    public List<Coupon> execute() {
        return couponRepositoryPort.findAll();
    }
}
