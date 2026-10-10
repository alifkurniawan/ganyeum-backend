package id.reshito.backend.modules.item.dto;

import java.math.BigInteger;
import java.util.UUID;


public record ItemResponse(
        UUID id,
        String itemName,
        BigInteger sellingPrice,
        BigInteger initialPrice,
        Long quantity
) {
}
