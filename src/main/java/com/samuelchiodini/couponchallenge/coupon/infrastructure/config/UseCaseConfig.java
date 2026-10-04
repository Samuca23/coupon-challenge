package com.samuelchiodini.couponchallenge.coupon.infrastructure.config;

import com.samuelchiodini.couponchallenge.coupon.application.port.in.CreateCouponInputPort;
import com.samuelchiodini.couponchallenge.coupon.application.port.out.CouponRepositoryPort;
import com.samuelchiodini.couponchallenge.coupon.application.usecase.CreateCouponUseCase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;

@Configuration
public class UseCaseConfig {

    @Bean
    public CreateCouponInputPort createCouponInputPort(CouponRepositoryPort couponRepositoryPort, Clock clock) {
        return new CreateCouponUseCase(couponRepositoryPort, clock);
    }
}
