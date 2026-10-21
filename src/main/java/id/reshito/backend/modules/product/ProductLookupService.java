package id.reshito.backend.modules.product;

import id.reshito.backend.common.exception.NotFoundException;
import id.reshito.backend.modules.product.model.Product;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductLookupService {
    private final ProductRepository productRepository;

    public Product findActiveForSeller(String sellerUid, UUID productId) {
        return productRepository.findByIdAndUidAndActive(productId, sellerUid, true)
                .orElseThrow(NotFoundException::new);
    }

    public List<Product> findAllActiveForSeller(String sellerUid, List<UUID> productIds) {
        return productRepository.findAllByIdInAndUidAndActive(productIds, sellerUid, true);
    }
}
