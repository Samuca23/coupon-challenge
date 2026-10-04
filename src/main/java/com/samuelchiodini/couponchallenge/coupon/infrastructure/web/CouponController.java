package com.samuelchiodini.couponchallenge.coupon.infrastructure.web;

import com.samuelchiodini.couponchallenge.coupon.application.port.in.CreateCouponInputPort;
import com.samuelchiodini.couponchallenge.coupon.application.port.in.DeleteCouponInputPort;
import com.samuelchiodini.couponchallenge.coupon.application.usecase.CreateCouponCommand;
import com.samuelchiodini.couponchallenge.coupon.domain.Coupon;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/coupon")
@Tag(name = "Coupon", description = "Operações de cupons de desconto")
public class CouponController {

    private final CreateCouponInputPort createCouponInputPort;
    private final DeleteCouponInputPort deleteCouponInputPort;

    public CouponController(CreateCouponInputPort createCouponInputPort,
                             DeleteCouponInputPort deleteCouponInputPort) {
        this.createCouponInputPort = createCouponInputPort;
        this.deleteCouponInputPort = deleteCouponInputPort;
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
}
