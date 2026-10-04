package com.samuelchiodini.couponchallenge.coupon.domain;

import com.samuelchiodini.couponchallenge.coupon.domain.exceptions.InvalidCouponCodeException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CouponCodeTest {

    @Test
    void normalizesSpecialCharactersKeepingSixAlphanumerics() {
        CouponCode code = CouponCode.of("ABC-123");

        assertThat(code.value()).isEqualTo("ABC123");
    }

    @Test
    void acceptsAlreadyCleanSixCharacterCode() {
        CouponCode code = CouponCode.of("ABCDEF");

        assertThat(code.value()).isEqualTo("ABCDEF");
    }

    @Test
    void rejectsCodeThatResultsInFewerThanSixAlphanumerics() {
        assertThatThrownBy(() -> CouponCode.of("@@-12"))
                .isInstanceOf(InvalidCouponCodeException.class);
    }

    @Test
    void rejectsCodeThatResultsInMoreThanSixAlphanumerics() {
        assertThatThrownBy(() -> CouponCode.of("AB!!CD@EFG"))
                .isInstanceOf(InvalidCouponCodeException.class);
    }

    @Test
    void rejectsCodeMadeOnlyOfSpecialCharacters() {
        assertThatThrownBy(() -> CouponCode.of("!!!---"))
                .isInstanceOf(InvalidCouponCodeException.class);
    }
}
