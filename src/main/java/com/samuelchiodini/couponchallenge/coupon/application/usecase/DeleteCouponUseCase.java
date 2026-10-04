package com.samuelchiodini.couponchallenge.coupon.application.usecase;

import com.samuelchiodini.couponchallenge.coupon.application.port.in.DeleteCouponInputPort;
import com.samuelchiodini.couponchallenge.coupon.application.port.out.CouponRepositoryPort;
import com.samuelchiodini.couponchallenge.coupon.domain.Coupon;
import com.samuelchiodini.couponchallenge.coupon.domain.exceptions.CouponNotFoundException;

import java.util.UUID;

public class DeleteCouponUseCase implements DeleteCouponInputPort {

    private final CouponRepositoryPort couponRepositoryPort;

    public DeleteCouponUseCase(CouponRepositoryPort couponRepositoryPort) {
        this.couponRepositoryPort = couponRepositoryPort;
    }

    @Override
    public void execute(UUID couponId) {
        Coupon coupon = couponRepositoryPort.findById(couponId)
                .orElseThrow(() -> new CouponNotFoundException(couponId));

        coupon.delete();

        couponRepositoryPort.save(coupon);
    }
}
