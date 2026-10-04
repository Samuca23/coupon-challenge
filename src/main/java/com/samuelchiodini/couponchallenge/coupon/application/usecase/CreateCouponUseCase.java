package com.samuelchiodini.couponchallenge.coupon.application.usecase;

import com.samuelchiodini.couponchallenge.coupon.application.port.in.CreateCouponInputPort;
import com.samuelchiodini.couponchallenge.coupon.application.port.out.CouponRepositoryPort;
import com.samuelchiodini.couponchallenge.coupon.domain.Coupon;

import java.time.Clock;

public class CreateCouponUseCase implements CreateCouponInputPort {

    private final CouponRepositoryPort couponRepositoryPort;
    private final Clock clock;

    public CreateCouponUseCase(CouponRepositoryPort couponRepositoryPort, Clock clock) {
        this.couponRepositoryPort = couponRepositoryPort;
        this.clock = clock;
    }

    @Override
    public Coupon execute(CreateCouponCommand command) {
        Coupon coupon = Coupon.create(
                command.rawCode(),
                command.description(),
                command.discountValue(),
                command.expirationDate(),
                command.published(),
                clock);

        return couponRepositoryPort.save(coupon);
    }
}
