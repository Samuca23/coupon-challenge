package com.samuelchiodini.couponchallenge.coupon.domain;

import com.samuelchiodini.couponchallenge.coupon.domain.exceptions.InvalidDiscountValueException;

import java.math.BigDecimal;
import java.util.Objects;

public final class DiscountValue {

    private static final BigDecimal MINIMUM = new BigDecimal("0.5");

    private final BigDecimal value;

    private DiscountValue(BigDecimal value) {
        this.value = value;
    }

    public static DiscountValue of(BigDecimal raw) {
        if (raw == null || raw.compareTo(MINIMUM) < 0) {
            throw new InvalidDiscountValueException("discountValue deve ser maior ou igual a 0.5");
        }
        return new DiscountValue(raw);
    }

    public BigDecimal value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DiscountValue other)) return false;
        return value.compareTo(other.value) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value.stripTrailingZeros());
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
