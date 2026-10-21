package id.reshito.backend.modules.inventory.dto;

import java.util.UUID;

public record StockResponse(UUID productId, Integer quantity) {}
