package id.reshito.backend.modules.inventory.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public record StockQuantityRequest(@NotNull @PositiveOrZero Integer quantity) {}
