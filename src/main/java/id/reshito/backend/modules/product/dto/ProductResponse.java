package id.reshito.backend.modules.product.dto;

import java.math.BigInteger;
import java.util.UUID;


public record ProductResponse(
        UUID id,
        String productName,
        BigInteger sellingPrice,
        BigInteger initialPrice,
        Long quantity
) {
}
