package id.reshito.backend.modules.item.dto;

import java.math.BigInteger;


public record ItemResponse(
        Long id,
        String itemName,
        BigInteger sellingPrice,
        BigInteger initialPrice,
        Integer quantity
) {
}
