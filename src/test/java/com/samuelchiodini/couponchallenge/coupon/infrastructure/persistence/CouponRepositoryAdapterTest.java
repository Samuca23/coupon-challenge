package com.samuelchiodini.couponchallenge.coupon.infrastructure.persistence;

import com.samuelchiodini.couponchallenge.coupon.domain.Coupon;
import com.samuelchiodini.couponchallenge.coupon.domain.CouponStatus;
import com.samuelchiodini.couponchallenge.coupon.domain.exceptions.CouponAlreadyDeletedException;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.OptimisticLockingFailureException;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
class CouponRepositoryAdapterTest {

    private static final Instant NOW = Instant.parse("2026-01-01T00:00:00Z");
    private final Clock fixedClock = Clock.fixed(NOW, ZoneOffset.UTC);

    @Autowired
    private CouponJpaRepository couponJpaRepository;

    @Autowired
    private EntityManager entityManager;

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

    @Test
    void updatesAnExistingCouponWithoutConflictAndIncrementsVersion() {
        CouponRepositoryAdapter adapter = new CouponRepositoryAdapter(couponJpaRepository);
        Coupon created = Coupon.create("ABC-123", "desc", new BigDecimal("1"), NOW.plusMillis(1), false, fixedClock);
        Coupon saved = adapter.save(created);
        flushAndClear();
        Long versionAfterInsert = couponJpaRepository.findById(saved.getId()).orElseThrow().getVersion();

        Coupon loaded = adapter.findById(saved.getId()).orElseThrow();
        loaded.delete();
        adapter.save(loaded);
        flushAndClear();

        CouponJpaEntity afterUpdate = couponJpaRepository.findById(saved.getId()).orElseThrow();
        assertThat(afterUpdate.getStatus()).isEqualTo(CouponStatus.DELETED.name());
        assertThat(afterUpdate.getVersion()).isGreaterThan(versionAfterInsert);
    }

    @Test
    void secondConcurrentSaveWithStaleVersionIsResolvedAsAlreadyDeleted() {
        CouponRepositoryAdapter adapter = new CouponRepositoryAdapter(couponJpaRepository);
        Coupon created = Coupon.create("ABC-123", "desc", new BigDecimal("1"), NOW.plusMillis(1), false, fixedClock);
        Coupon saved = adapter.save(created);
        flushAndClear();

        Coupon firstReader = adapter.findById(saved.getId()).orElseThrow();
        flushAndClear();
        Coupon secondReader = adapter.findById(saved.getId()).orElseThrow();
        flushAndClear();
        firstReader.delete();
        secondReader.delete();

        adapter.save(firstReader);
        flushAndClear();

        assertThatThrownBy(() -> adapter.save(secondReader))
                .isInstanceOf(CouponAlreadyDeletedException.class);
    }

    @Test
    void staleVersionConflictNotCausedByDeleteRethrowsOriginalException() {
        CouponRepositoryAdapter adapter = new CouponRepositoryAdapter(couponJpaRepository);
        Coupon created = Coupon.create("ABC-123", "desc", new BigDecimal("1"), NOW.plusMillis(1), false, fixedClock);
        Coupon saved = adapter.save(created);
        flushAndClear();

        Coupon staleReader = adapter.findById(saved.getId()).orElseThrow();
        flushAndClear();

        CouponJpaEntity unrelatedChange = couponJpaRepository.findById(saved.getId()).orElseThrow();
        unrelatedChange.setPublished(true);
        couponJpaRepository.save(unrelatedChange);
        flushAndClear();

        staleReader.delete();

        assertThatThrownBy(() -> adapter.save(staleReader))
                .isInstanceOf(OptimisticLockingFailureException.class)
                .isNotInstanceOf(CouponAlreadyDeletedException.class);
    }

    private void flushAndClear() {
        entityManager.flush();
        entityManager.clear();
    }
}
