package com.samuelchiodini.couponchallenge.coupon.infrastructure.web;

import com.samuelchiodini.couponchallenge.coupon.application.port.in.CreateCouponInputPort;
import com.samuelchiodini.couponchallenge.coupon.application.port.in.DeleteCouponInputPort;
import com.samuelchiodini.couponchallenge.coupon.application.port.in.ListCouponsInputPort;
import com.samuelchiodini.couponchallenge.coupon.application.usecase.CreateCouponCommand;
import com.samuelchiodini.couponchallenge.coupon.domain.Coupon;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/coupon")
@Tag(name = "Coupon", description = "Operações de cupons de desconto")
public class CouponController {

    private final CreateCouponInputPort createCouponInputPort;
    private final DeleteCouponInputPort deleteCouponInputPort;
    private final ListCouponsInputPort listCouponsInputPort;

    public CouponController(CreateCouponInputPort createCouponInputPort,
                             DeleteCouponInputPort deleteCouponInputPort,
                             ListCouponsInputPort listCouponsInputPort) {
        this.createCouponInputPort = createCouponInputPort;
        this.deleteCouponInputPort = deleteCouponInputPort;
        this.listCouponsInputPort = listCouponsInputPort;
    }

    @PostMapping
    @Operation(summary = "Cria um novo cupom de desconto")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Cupom criado com sucesso",
                    content = @Content(schema = @Schema(implementation = CouponResponse.class))),
            @ApiResponse(responseCode = "400", description = "Requisição inválida (campo ausente, "
                    + "code/discountValue/expirationDate fora das regras de negócio, ou corpo malformado)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<CouponResponse> create(@Valid @RequestBody CreateCouponRequest request) {
        CreateCouponCommand command = new CreateCouponCommand(
                request.code(),
                request.description(),
                request.discountValue(),
                request.expirationDate(),
                request.published());

        Coupon coupon = createCouponInputPort.execute(command);

        return ResponseEntity.status(HttpStatus.CREATED).body(CouponResponse.from(coupon));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Deleta (soft delete) um cupom de desconto existente")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Cupom deletado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Cupom não encontrado",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "Cupom já está deletado",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        deleteCouponInputPort.execute(id);

        return ResponseEntity.noContent().build();
    }

    @GetMapping
    @Operation(summary = "Lista todos os cupons cadastrados",
            description = "Endpoint auxiliar fora do escopo formal do desafio, criado só para facilitar "
                    + "testes manuais via Swagger (o desafio pede apenas Create e Delete).")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista de cupons",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = CouponResponse.class))))
    })
    public ResponseEntity<List<CouponResponse>> list() {
        List<CouponResponse> coupons = listCouponsInputPort.execute().stream()
                .map(CouponResponse::from)
                .toList();

        return ResponseEntity.ok(coupons);
    }
}
