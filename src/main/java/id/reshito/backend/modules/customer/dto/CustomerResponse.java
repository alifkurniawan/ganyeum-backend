package id.reshito.backend.modules.customer.dto;

import java.util.UUID;

public record CustomerResponse(UUID id, String name, String phoneNumber) {}
