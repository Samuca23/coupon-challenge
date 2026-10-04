package com.samuelchiodini.couponchallenge.coupon.infrastructure.web;

import tools.jackson.databind.ObjectMapper;
import com.samuelchiodini.couponchallenge.coupon.domain.CouponStatus;
import com.samuelchiodini.couponchallenge.coupon.infrastructure.persistence.CouponJpaRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class CouponControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CouponJpaRepository couponJpaRepository;

    private String futureDate() {
        return Instant.now().plus(1, ChronoUnit.DAYS).toString();
    }

    private UUID createCoupon() throws Exception {
        Map<String, Object> body = Map.of(
                "code", "ABC123",
                "description", "desc",
                "discountValue", new BigDecimal("0.8"),
                "expirationDate", futureDate());

        MvcResult result = mockMvc.perform(post("/coupon")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(body)))
                .andReturn();

        String id = objectMapper.readTree(result.getResponse().getContentAsString()).get("id").asText();
        return UUID.fromString(id);
    }

    @Test
    void createsCouponWithValidPayload() throws Exception {
        Map<String, Object> body = Map.of(
                "code", "ABC-123",
                "description", "desc",
                "discountValue", new BigDecimal("0.8"),
                "expirationDate", futureDate());

        mockMvc.perform(post("/coupon")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.code").value("ABC123"))
                .andExpect(jsonPath("$.status").value("ACTIVE"))
                .andExpect(jsonPath("$.redeemed").value(false))
                .andExpect(jsonPath("$.published").value(false))
                .andExpect(jsonPath("$.id").isNotEmpty());
    }

    @Test
    void rejectsRequestMissingCode() throws Exception {
        Map<String, Object> body = Map.of(
                "description", "desc",
                "discountValue", new BigDecimal("0.8"),
                "expirationDate", futureDate());

        mockMvc.perform(post("/coupon")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.fields", hasSize(1)));
    }

    @Test
    void rejectsRequestMissingDescription() throws Exception {
        Map<String, Object> body = Map.of(
                "code", "ABC123",
                "discountValue", new BigDecimal("0.8"),
                "expirationDate", futureDate());

        mockMvc.perform(post("/coupon")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.fields", hasSize(1)));
    }

    @Test
    void rejectsRequestMissingDiscountValue() throws Exception {
        Map<String, Object> body = Map.of(
                "code", "ABC123",
                "description", "desc",
                "expirationDate", futureDate());

        mockMvc.perform(post("/coupon")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.fields", hasSize(1)));
    }

    @Test
    void rejectsRequestMissingExpirationDate() throws Exception {
        Map<String, Object> body = Map.of(
                "code", "ABC123",
                "description", "desc",
                "discountValue", new BigDecimal("0.8"));

        mockMvc.perform(post("/coupon")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.fields", hasSize(1)));
    }

    @Test
    void rejectsCodeThatNormalizesToFewerThanSixCharacters() throws Exception {
        Map<String, Object> body = Map.of(
                "code", "@@-12",
                "description", "desc",
                "discountValue", new BigDecimal("0.8"),
                "expirationDate", futureDate());

        mockMvc.perform(post("/coupon")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("INVALID_COUPON_CODE"));
    }

    @Test
    void rejectsCodeThatNormalizesToMoreThanSixCharacters() throws Exception {
        Map<String, Object> body = Map.of(
                "code", "AB!!CD@EFG",
                "description", "desc",
                "discountValue", new BigDecimal("0.8"),
                "expirationDate", futureDate());

        mockMvc.perform(post("/coupon")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("INVALID_COUPON_CODE"));
    }

    @Test
    void acceptsDiscountValueAtTheInclusiveMinimum() throws Exception {
        Map<String, Object> body = Map.of(
                "code", "ABC123",
                "description", "desc",
                "discountValue", new BigDecimal("0.5"),
                "expirationDate", futureDate());

        mockMvc.perform(post("/coupon")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isCreated());
    }

    @Test
    void rejectsDiscountValueBelowTheMinimum() throws Exception {
        Map<String, Object> body = Map.of(
                "code", "ABC123",
                "description", "desc",
                "discountValue", new BigDecimal("0.3"),
                "expirationDate", futureDate());

        mockMvc.perform(post("/coupon")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("INVALID_DISCOUNT_VALUE"));
    }

    @Test
    void rejectsExpirationDateInThePast() throws Exception {
        Map<String, Object> body = Map.of(
                "code", "ABC123",
                "description", "desc",
                "discountValue", new BigDecimal("0.8"),
                "expirationDate", Instant.now().minus(1, ChronoUnit.DAYS).toString());

        mockMvc.perform(post("/coupon")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("INVALID_EXPIRATION_DATE"));
    }

    @Test
    void rejectsMalformedDiscountValueType() throws Exception {
        String malformedJson = """
                {
                  "code": "ABC123",
                  "description": "desc",
                  "discountValue": "not-a-number",
                  "expirationDate": "%s"
                }
                """.formatted(futureDate());

        mockMvc.perform(post("/coupon")
                        .contentType("application/json")
                        .content(malformedJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("MALFORMED_REQUEST"));
    }

    @Test
    void defaultsPublishedToFalseWhenOmitted() throws Exception {
        Map<String, Object> body = Map.of(
                "code", "ABC123",
                "description", "desc",
                "discountValue", new BigDecimal("0.8"),
                "expirationDate", futureDate());

        mockMvc.perform(post("/coupon")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.published").value(false));
    }

    @Test
    void acceptsPublishedTrueExplicitly() throws Exception {
        Map<String, Object> body = Map.of(
                "code", "ABC123",
                "description", "desc",
                "discountValue", new BigDecimal("0.8"),
                "expirationDate", futureDate(),
                "published", true);

        mockMvc.perform(post("/coupon")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.published").value(true));
    }

    @Test
    void ignoresClientSuppliedStatusRedeemedAndId() throws Exception {
        String clientSuppliedId = "11111111-1111-1111-1111-111111111111";
        Map<String, Object> body = Map.of(
                "id", clientSuppliedId,
                "code", "ABC123",
                "description", "desc",
                "discountValue", new BigDecimal("0.8"),
                "expirationDate", futureDate(),
                "status", "DELETED",
                "redeemed", true);

        mockMvc.perform(post("/coupon")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("ACTIVE"))
                .andExpect(jsonPath("$.redeemed").value(false))
                .andExpect(jsonPath("$.id").value(org.hamcrest.Matchers.not(clientSuppliedId)));
    }

    @Test
    void deletesAnExistingActiveCoupon() throws Exception {
        UUID id = createCoupon();

        mockMvc.perform(delete("/coupon/{id}", id))
                .andExpect(status().isNoContent());

        assertThat(couponJpaRepository.findById(id)).isPresent();
        assertThat(couponJpaRepository.findById(id).orElseThrow().getStatus())
                .isEqualTo(CouponStatus.DELETED.name());
    }

    @Test
    void deletingTheSameCouponTwiceReturnsConflict() throws Exception {
        UUID id = createCoupon();

        mockMvc.perform(delete("/coupon/{id}", id))
                .andExpect(status().isNoContent());

        mockMvc.perform(delete("/coupon/{id}", id))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").value("COUPON_ALREADY_DELETED"));
    }

    @Test
    void deletingAnIdThatNeverExistedReturnsNotFound() throws Exception {
        UUID neverExisted = UUID.randomUUID();

        mockMvc.perform(delete("/coupon/{id}", neverExisted))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("COUPON_NOT_FOUND"));
    }

    @Test
    void deletingWithAMalformedIdReturnsBadRequest() throws Exception {
        mockMvc.perform(delete("/coupon/{id}", "abc"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("MALFORMED_REQUEST"));
    }
}
