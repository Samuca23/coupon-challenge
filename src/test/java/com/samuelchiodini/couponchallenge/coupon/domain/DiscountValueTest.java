package com.samuelchiodini.couponchallenge.coupon.domain;

import com.samuelchiodini.couponchallenge.coupon.domain.exceptions.InvalidDiscountValueException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DiscountValueTest {

    @Test
    void acceptsExactlyTheMinimumInclusiveLimit() {
        DiscountValue discountValue = DiscountValue.of(new BigDecimal("0.5"));

        assertThat(discountValue.value()).isEqualByComparingTo("0.5");
    }

    @Test
    void rejectsValueJustBelowTheMinimum() {
        assertThatThrownBy(() -> DiscountValue.of(new BigDecimal("0.49999")))
                .isInstanceOf(InvalidDiscountValueException.class);
    }

    @Test
    void rejectsZero() {
        assertThatThrownBy(() -> DiscountValue.of(BigDecimal.ZERO))
                .isInstanceOf(InvalidDiscountValueException.class);
    }

    @Test
    void rejectsNegativeValue() {
        assertThatThrownBy(() -> DiscountValue.of(new BigDecimal("-1")))
                .isInstanceOf(InvalidDiscountValueException.class);
    }

    @Test
    void acceptsLargeValueSinceThereIsNoMaximum() {
        DiscountValue discountValue = DiscountValue.of(new BigDecimal("1000"));

        assertThat(discountValue.value()).isEqualByComparingTo("1000");
    }
}
