package com.samuelchiodini.couponchallenge.coupon.application.usecase;

import com.samuelchiodini.couponchallenge.coupon.application.port.out.CouponRepositoryPort;
import com.samuelchiodini.couponchallenge.coupon.domain.Coupon;
import com.samuelchiodini.couponchallenge.coupon.domain.exceptions.InvalidDiscountValueException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CreateCouponUseCaseTest {

    private static final Instant NOW = Instant.parse("2026-01-01T00:00:00Z");
    private final Clock fixedClock = Clock.fixed(NOW, ZoneOffset.UTC);

    @Mock
    private CouponRepositoryPort couponRepositoryPort;

    @Test
    void savesCreatedCouponExactlyOnceForAValidCommand() {
        CreateCouponUseCase useCase = new CreateCouponUseCase(couponRepositoryPort, fixedClock);
        CreateCouponCommand command = new CreateCouponCommand(
                "ABC-123", "desc", new BigDecimal("1"), NOW.plusMillis(1), false);
        when(couponRepositoryPort.save(any(Coupon.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Coupon result = useCase.execute(command);

        ArgumentCaptor<Coupon> captor = ArgumentCaptor.forClass(Coupon.class);
        verify(couponRepositoryPort).save(captor.capture());
        assertThat(captor.getValue().getCode().value()).isEqualTo("ABC123");
        assertThat(result).isSameAs(captor.getValue());
    }

    @Test
    void neverSavesWhenDomainRuleIsViolated() {
        CreateCouponUseCase useCase = new CreateCouponUseCase(couponRepositoryPort, fixedClock);
        CreateCouponCommand invalidCommand = new CreateCouponCommand(
                "ABC-123", "desc", new BigDecimal("0.1"), NOW.plusMillis(1), false);

        assertThatThrownBy(() -> useCase.execute(invalidCommand))
                .isInstanceOf(InvalidDiscountValueException.class);

        verify(couponRepositoryPort, never()).save(any());
    }
}
