package id.reshito.backend.modules.product.dto;

import java.math.BigInteger;

public record ProductRequest(
        String productName,
        BigInteger sellingPrice,
        BigInteger initialPrice,
        Integer quantity
) {
}
