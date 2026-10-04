package com.samuelchiodini.couponchallenge.coupon.infrastructure.persistence;

import com.samuelchiodini.couponchallenge.coupon.application.port.out.CouponRepositoryPort;
import com.samuelchiodini.couponchallenge.coupon.domain.Coupon;
import com.samuelchiodini.couponchallenge.coupon.domain.CouponCode;
import com.samuelchiodini.couponchallenge.coupon.domain.CouponStatus;
import com.samuelchiodini.couponchallenge.coupon.domain.DiscountValue;
import com.samuelchiodini.couponchallenge.coupon.domain.exceptions.CouponAlreadyDeletedException;
import com.samuelchiodini.couponchallenge.coupon.domain.exceptions.CouponNotFoundException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
public class CouponRepositoryAdapter implements CouponRepositoryPort {

    private final CouponJpaRepository couponJpaRepository;

    public CouponRepositoryAdapter(CouponJpaRepository couponJpaRepository) {
        this.couponJpaRepository = couponJpaRepository;
    }

    @Override
    public Coupon save(Coupon coupon) {
        try {
            CouponJpaEntity saved = couponJpaRepository.save(toEntity(coupon));
            return toDomain(saved);
        } catch (OptimisticLockingFailureException ex) {
            CouponJpaEntity fresh = couponJpaRepository.findById(coupon.getId())
                    .orElseThrow(() -> new CouponNotFoundException(coupon.getId()));
            if (CouponStatus.DELETED.name().equals(fresh.getStatus())) {
                throw new CouponAlreadyDeletedException(coupon.getId());
            }
            throw ex;
        }
    }

    @Override
    public Optional<Coupon> findById(UUID id) {
        return couponJpaRepository.findById(id).map(this::toDomain);
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
        entity.setVersion(coupon.getVersion());
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
                entity.isRedeemed(),
                entity.getVersion());
    }
}
