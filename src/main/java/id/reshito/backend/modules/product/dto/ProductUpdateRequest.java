package id.reshito.backend.modules.product.dto;

import java.math.BigInteger;

public record ProductUpdateRequest(
        String productName,
        BigInteger sellingPrice,
        BigInteger initialPrice
) {
}