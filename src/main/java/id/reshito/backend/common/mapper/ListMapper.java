package id.reshito.backend.common.mapper;

import id.reshito.backend.common.dto.ListResponse;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

@Component
public class ListMapper {
    public ListResponse toResponse(Integer currentPage, Page<?> page) {
        return new ListResponse(
                page.getContent(),
                currentPage,
                page.getTotalPages(),
                page.getTotalElements()
        );
    }
}
