package com.samuelchiodini.couponchallenge.coupon.infrastructure.persistence;

import com.samuelchiodini.couponchallenge.coupon.application.port.out.CouponRepositoryPort;
import com.samuelchiodini.couponchallenge.coupon.domain.Coupon;
import com.samuelchiodini.couponchallenge.coupon.domain.CouponCode;
import com.samuelchiodini.couponchallenge.coupon.domain.CouponStatus;
import com.samuelchiodini.couponchallenge.coupon.domain.DiscountValue;
import org.springframework.stereotype.Component;

@Component
public class CouponRepositoryAdapter implements CouponRepositoryPort {

    private final CouponJpaRepository couponJpaRepository;

    public CouponRepositoryAdapter(CouponJpaRepository couponJpaRepository) {
        this.couponJpaRepository = couponJpaRepository;
    }

    @Override
    public Coupon save(Coupon coupon) {
        CouponJpaEntity saved = couponJpaRepository.save(toEntity(coupon));
        return toDomain(saved);
    }

    private CouponJpaEntity toEntity(Coupon coupon) {
        CouponJpaEntity entity = new CouponJpaEntity();
        entity.setId(coupon.getId());
        entity.setCode(coupon.getCode().value());
        entity.setDescription(coupon.getDescription());
        entity.setDiscountValue(coupon.getDiscountValue().value());
        entity.setExpirationDate(coupon.getExpirationDate());
        entity.setStatus(coupon.getStatus().name());
        entity.setPublished(coupon.isPublished());
        entity.setRedeemed(coupon.isRedeemed());
        return entity;
    }

    private Coupon toDomain(CouponJpaEntity entity) {
        return Coupon.reconstitute(
                entity.getId(),
                CouponCode.of(entity.getCode()),
                entity.getDescription(),
                DiscountValue.of(entity.getDiscountValue()),
                entity.getExpirationDate(),
                CouponStatus.valueOf(entity.getStatus()),
                entity.isPublished(),
                entity.isRedeemed());
    }
}
