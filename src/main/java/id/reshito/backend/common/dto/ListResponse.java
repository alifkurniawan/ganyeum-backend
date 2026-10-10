package id.reshito.backend.common.dto;

import java.util.List;

public record ListResponse(
        List<?> data,
        Integer currentPage,
        Integer totalPage,
        Long totalData
) {
}
