package id.reshito.backend.modules.item.dto;

import java.math.BigInteger;

public record ItemRequest(
        String itemName,
        BigInteger sellingPrice,
        BigInteger initialPrice,
        Integer quantity
) {
}
