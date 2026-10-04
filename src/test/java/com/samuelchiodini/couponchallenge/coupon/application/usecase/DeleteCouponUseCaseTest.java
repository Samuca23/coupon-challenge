package com.samuelchiodini.couponchallenge.coupon.application.usecase;

import com.samuelchiodini.couponchallenge.coupon.application.port.out.CouponRepositoryPort;
import com.samuelchiodini.couponchallenge.coupon.domain.Coupon;
import com.samuelchiodini.couponchallenge.coupon.domain.CouponStatus;
import com.samuelchiodini.couponchallenge.coupon.domain.exceptions.CouponAlreadyDeletedException;
import com.samuelchiodini.couponchallenge.coupon.domain.exceptions.CouponNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DeleteCouponUseCaseTest {

    private static final Instant NOW = Instant.parse("2026-01-01T00:00:00Z");
    private final Clock fixedClock = Clock.fixed(NOW, ZoneOffset.UTC);

    @Mock
    private CouponRepositoryPort couponRepositoryPort;

    private Coupon activeCoupon() {
        return Coupon.create("ABC123", "desc", new BigDecimal("1"), NOW.plusMillis(1), false, fixedClock);
    }

    @Test
    void deletesAnExistingActiveCouponExactlyOnce() {
        Coupon coupon = activeCoupon();
        when(couponRepositoryPort.findById(coupon.getId())).thenReturn(Optional.of(coupon));

        DeleteCouponUseCase useCase = new DeleteCouponUseCase(couponRepositoryPort);
        useCase.execute(coupon.getId());

        ArgumentCaptor<Coupon> captor = ArgumentCaptor.forClass(Coupon.class);
        verify(couponRepositoryPort).save(captor.capture());
        assertThat(captor.getValue().getStatus()).isEqualTo(CouponStatus.DELETED);
    }

    @Test
    void throwsNotFoundAndNeverSavesWhenCouponDoesNotExist() {
        UUID missingId = UUID.randomUUID();
        when(couponRepositoryPort.findById(missingId)).thenReturn(Optional.empty());

        DeleteCouponUseCase useCase = new DeleteCouponUseCase(couponRepositoryPort);

        assertThatThrownBy(() -> useCase.execute(missingId))
                .isInstanceOf(CouponNotFoundException.class);

        verify(couponRepositoryPort, never()).save(any());
    }

    @Test
    void propagatesAlreadyDeletedAndNeverSavesWhenCouponIsAlreadyDeleted() {
        Coupon coupon = activeCoupon();
        coupon.delete();
        when(couponRepositoryPort.findById(coupon.getId())).thenReturn(Optional.of(coupon));

        DeleteCouponUseCase useCase = new DeleteCouponUseCase(couponRepositoryPort);

        assertThatThrownBy(() -> useCase.execute(coupon.getId()))
                .isInstanceOf(CouponAlreadyDeletedException.class);

        verify(couponRepositoryPort, never()).save(any());
    }
}
