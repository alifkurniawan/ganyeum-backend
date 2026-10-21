package id.reshito.backend.modules.product;


import id.reshito.backend.common.dto.GetRequest;
import id.reshito.backend.common.dto.ListResponse;
import id.reshito.backend.common.exception.NotFoundException;
import id.reshito.backend.common.mapper.ListMapper;
import id.reshito.backend.modules.inventory.StockService;
import id.reshito.backend.modules.product.dto.ProductRequest;
import id.reshito.backend.modules.product.dto.ProductResponse;
import id.reshito.backend.modules.product.dto.ProductUpdateRequest;
import id.reshito.backend.modules.product.mapper.ProductMapper;
import id.reshito.backend.modules.product.model.Product;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;
import java.util.List;

@AllArgsConstructor
@Service
public class ProductService {
    private final ProductRepository productRepository;
    private final StockService stockService;
    private final ProductMapper productMapper;
    private final ListMapper listMapper;

    @Transactional
    public ProductResponse addProduct(String uid, ProductRequest request) {
        Product product = Product.builder()
                .productName(request.productName())
                .initialPrice(request.initialPrice())
                .sellingPrice(request.sellingPrice())
                .uid(uid)
                .build();

        this.productRepository.save(product);
        int quantity = stockService.initializeStock(product, request.quantity());
        return productMapper.toResponse(product, quantity);
    }

    public List<Product> findActiveProductsForSeller(String uid, List<UUID> productIds) {
        return productRepository.findAllByIdInAndUidAndActive(productIds, uid, true);
    }

    public Product findActiveProductForSeller(String uid, UUID productId) {
        return productRepository.findByIdAndUidAndActive(productId, uid, true)
                .orElseThrow(NotFoundException::new);
    }

    public ListResponse getProducts(String uid, GetRequest request) {
        var result = productRepository.findProductByProductNameAndUidWithQuantity(
                uid, request.query(),
                PageRequest.of(
                        request.page() - 1,
                        request.size(),
                        Sort.by(request.sortBy()).ascending()
                )
        );

        return listMapper.toResponse(request.page(), result);
    }


    public ProductResponse updateProduct(String uid, UUID itemId, ProductUpdateRequest request) {
        Product product = productRepository.findByIdAndUidAndActive(itemId, uid, true).orElseThrow(NotFoundException::new);
        product.setProductName(request.productName());
        product.setInitialPrice(request.initialPrice());
        product.setSellingPrice(request.sellingPrice());
        this.productRepository.save(product);
        return productMapper.toResponse(product, stockService.getStock(product));
    }

    @Transactional
    public void deleteProduct(String uid, UUID itemId) {
        Product product = productRepository.findByIdAndUidAndActive(itemId, uid, true)
                .orElseThrow(NotFoundException::new);
        product.setActive(false);
        product.setDeletedAt(Instant.now());
        productRepository.save(product);
    }

    public ListResponse getProductsBySeller(UUID sellerId, @Valid GetRequest request) {
        var result = productRepository.findProductByProductNameAndSellerIdWithQuantity(
                sellerId, request.query(),
                PageRequest.of(
                        request.page() - 1,
                        request.size(),
                        Sort.by(request.sortBy()).ascending()
                )
        );

        return listMapper.toResponse(request.page(), result);
    }
}
