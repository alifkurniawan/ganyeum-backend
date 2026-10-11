
package id.reshito.backend.modules.product.mapper;

import id.reshito.backend.modules.product.dto.ProductResponse;
import id.reshito.backend.modules.product.model.Product;
import org.springframework.stereotype.Component;

@Component
public class ProductMapper {

    public ProductResponse toResponse(Product product, Integer quantity) {
        return new ProductResponse(
                product.getId(),
                product.getProductName(),
                product.getSellingPrice(),
                product.getInitialPrice(),
                quantity.longValue()
        );
    }
}