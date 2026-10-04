package com.samuelchiodini.couponchallenge.coupon.domain;

import com.samuelchiodini.couponchallenge.coupon.domain.exceptions.CouponAlreadyDeletedException;
import com.samuelchiodini.couponchallenge.coupon.domain.exceptions.InvalidExpirationDateException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CouponTest {

    private static final Instant NOW = Instant.parse("2026-01-01T00:00:00Z");
    private final Clock fixedClock = Clock.fixed(NOW, ZoneOffset.UTC);

    @Test
    void rejectsExpirationDateInThePast() {
        Instant past = NOW.minus(Duration.ofDays(1));

        assertThatThrownBy(() -> Coupon.create("ABC123", "desc", new BigDecimal("1"), past, false, fixedClock))
                .isInstanceOf(InvalidExpirationDateException.class);
    }

    @Test
    void rejectsExpirationDateEqualToNow() {
        assertThatThrownBy(() -> Coupon.create("ABC123", "desc", new BigDecimal("1"), NOW, false, fixedClock))
                .isInstanceOf(InvalidExpirationDateException.class);
    }

    @Test
    void acceptsExpirationDateOneMillisecondInTheFuture() {
        Instant future = NOW.plusMillis(1);

        Coupon coupon = Coupon.create("ABC123", "desc", new BigDecimal("1"), future, false, fixedClock);

        assertThat(coupon.getExpirationDate()).isEqualTo(future);
    }

    @Test
    void defaultsPublishedToFalseWhenNotInformed() {
        Coupon coupon = Coupon.create("ABC123", "desc", new BigDecimal("1"), NOW.plusMillis(1), null, fixedClock);

        assertThat(coupon.isPublished()).isFalse();
    }

    @Test
    void acceptsPublishedTrueExplicitly() {
        Coupon coupon = Coupon.create("ABC123", "desc", new BigDecimal("1"), NOW.plusMillis(1), true, fixedClock);

        assertThat(coupon.isPublished()).isTrue();
    }

    @Test
    void alwaysStartsActive() {
        Coupon coupon = Coupon.create("ABC123", "desc", new BigDecimal("1"), NOW.plusMillis(1), false, fixedClock);

        assertThat(coupon.getStatus()).isEqualTo(CouponStatus.ACTIVE);
    }

    @Test
    void alwaysStartsNotRedeemed() {
        Coupon coupon = Coupon.create("ABC123", "desc", new BigDecimal("1"), NOW.plusMillis(1), false, fixedClock);

        assertThat(coupon.isRedeemed()).isFalse();
    }

    @Test
    void generatesUniqueNonNullIdForEachCreation() {
        Coupon first = Coupon.create("ABC123", "desc", new BigDecimal("1"), NOW.plusMillis(1), false, fixedClock);
        Coupon second = Coupon.create("ABC123", "desc", new BigDecimal("1"), NOW.plusMillis(1), false, fixedClock);

        assertThat(first.getId()).isNotNull();
        assertThat(second.getId()).isNotNull();
        assertThat(first.getId()).isNotEqualTo(second.getId());
    }

    @Test
    void deletingAnActiveCouponTransitionsItToDeleted() {
        Coupon coupon = Coupon.create("ABC123", "desc", new BigDecimal("1"), NOW.plusMillis(1), false, fixedClock);

        coupon.delete();

        assertThat(coupon.getStatus()).isEqualTo(CouponStatus.DELETED);
    }

    @Test
    void deletingAnInactiveCouponTransitionsItToDeleted() {
        Coupon active = Coupon.create("ABC123", "desc", new BigDecimal("1"), NOW.plusMillis(1), false, fixedClock);
        Coupon inactive = Coupon.withStatusForTesting(active, CouponStatus.INACTIVE);

        inactive.delete();

        assertThat(inactive.getStatus()).isEqualTo(CouponStatus.DELETED);
    }

    @Test
    void deletingAnAlreadyDeletedCouponThrowsAndKeepsStatusDeleted() {
        Coupon coupon = Coupon.create("ABC123", "desc", new BigDecimal("1"), NOW.plusMillis(1), false, fixedClock);
        coupon.delete();

        assertThatThrownBy(coupon::delete)
                .isInstanceOf(CouponAlreadyDeletedException.class);
        assertThat(coupon.getStatus()).isEqualTo(CouponStatus.DELETED);
    }

    @Test
    void deletingAnExpiredButActiveCouponStillSucceeds() {
        Instant pastExpiration = NOW.minus(Duration.ofDays(30));
        Coupon expiredButActive = Coupon.reconstitute(
                UUID.randomUUID(),
                CouponCode.of("ABC123"),
                "desc",
                DiscountValue.of(new BigDecimal("1")),
                pastExpiration,
                CouponStatus.ACTIVE,
                false,
                false,
                null);

        expiredButActive.delete();

        assertThat(expiredButActive.getStatus()).isEqualTo(CouponStatus.DELETED);
    }

    @Test
    void deletingAPublishedCouponSucceeds() {
        Coupon published = Coupon.create("ABC123", "desc", new BigDecimal("1"), NOW.plusMillis(1), true, fixedClock);

        published.delete();

        assertThat(published.getStatus()).isEqualTo(CouponStatus.DELETED);
    }

    @Test
    void deletingARedeemedCouponSucceeds() {
        Coupon redeemed = Coupon.reconstitute(
                UUID.randomUUID(),
                CouponCode.of("ABC123"),
                "desc",
                DiscountValue.of(new BigDecimal("1")),
                NOW.plusMillis(1),
                CouponStatus.ACTIVE,
                false,
                true,
                null);

        redeemed.delete();

        assertThat(redeemed.getStatus()).isEqualTo(CouponStatus.DELETED);
    }
}
