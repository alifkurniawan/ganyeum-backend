package id.reshito.backend.common.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

public record GetRequest (
        @Size(max=100)
        String query,
        @Size(max=100)
        String sortBy,

        @Min(1)
        Integer page,

        @Min(1)
        @Max(100)
        Integer size
) {
    public GetRequest{
        page = page == null? 1: page;
        size = size == null? 100: size;
        query = (query == null || query.isBlank())? null: query.trim();
        sortBy = (sortBy == null || sortBy.isBlank())? "id": sortBy.trim();
    }
}
