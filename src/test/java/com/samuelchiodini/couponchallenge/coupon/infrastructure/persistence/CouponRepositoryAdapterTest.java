package com.samuelchiodini.couponchallenge.coupon.infrastructure.persistence;

import com.samuelchiodini.couponchallenge.coupon.domain.Coupon;
import com.samuelchiodini.couponchallenge.coupon.domain.CouponStatus;
import org.junit.jupiter.api.Test;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class CouponRepositoryAdapterTest {

    private static final Instant NOW = Instant.parse("2026-01-01T00:00:00Z");
    private final Clock fixedClock = Clock.fixed(NOW, ZoneOffset.UTC);

    @Autowired
    private CouponJpaRepository couponJpaRepository;

    @Test
    void savesAndReloadsANewCouponPreservingItsNormalizedState() {
        CouponRepositoryAdapter adapter = new CouponRepositoryAdapter(couponJpaRepository);
        Coupon coupon = Coupon.create("ABC-123", "desc", new BigDecimal("1"), NOW.plusMillis(1), false, fixedClock);

        Coupon saved = adapter.save(coupon);

        Optional<CouponJpaEntity> persisted = couponJpaRepository.findById(saved.getId());
        assertThat(persisted).isPresent();
        assertThat(persisted.get().getCode()).isEqualTo("ABC123");
        assertThat(persisted.get().getStatus()).isEqualTo(CouponStatus.ACTIVE.name());
        assertThat(persisted.get().isRedeemed()).isFalse();
    }
}
