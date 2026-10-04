package com.samuelchiodini.couponchallenge.coupon.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CouponJpaRepository extends JpaRepository<CouponJpaEntity, UUID> {
}
