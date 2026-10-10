
package id.reshito.backend.modules.item.mapper;

import id.reshito.backend.modules.item.dto.ItemResponse;
import id.reshito.backend.modules.item.model.Item;
import org.springframework.stereotype.Component;

@Component
public class ItemMapper {

    public ItemResponse toResponse(Item item, Integer quantity) {
        return new ItemResponse(
                item.getId(),
                item.getItemName(),
                item.getSellingPrice(),
                item.getInitialPrice(),
                quantity.longValue()
        );
    }
}