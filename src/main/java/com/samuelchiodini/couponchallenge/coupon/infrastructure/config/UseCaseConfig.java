package com.samuelchiodini.couponchallenge.coupon.infrastructure.config;

import com.samuelchiodini.couponchallenge.coupon.application.port.in.CreateCouponInputPort;
import com.samuelchiodini.couponchallenge.coupon.application.port.in.DeleteCouponInputPort;
import com.samuelchiodini.couponchallenge.coupon.application.port.in.ListCouponsInputPort;
import com.samuelchiodini.couponchallenge.coupon.application.port.out.CouponRepositoryPort;
import com.samuelchiodini.couponchallenge.coupon.application.usecase.CreateCouponUseCase;
import com.samuelchiodini.couponchallenge.coupon.application.usecase.DeleteCouponUseCase;
import com.samuelchiodini.couponchallenge.coupon.application.usecase.ListCouponsUseCase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;

@Configuration
public class UseCaseConfig {

    @Bean
    public CreateCouponInputPort createCouponInputPort(CouponRepositoryPort couponRepositoryPort, Clock clock) {
        return new CreateCouponUseCase(couponRepositoryPort, clock);
    }

    @Bean
    public DeleteCouponInputPort deleteCouponInputPort(CouponRepositoryPort couponRepositoryPort) {
        return new DeleteCouponUseCase(couponRepositoryPort);
    }

    @Bean
    public ListCouponsInputPort listCouponsInputPort(CouponRepositoryPort couponRepositoryPort) {
        return new ListCouponsUseCase(couponRepositoryPort);
    }
}
