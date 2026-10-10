package id.reshito.backend.modules.item.dto;

import java.math.BigInteger;

public record ItemUpdateRequest(
        String itemName,
        BigInteger sellingPrice,
        BigInteger initialPrice
) {
}