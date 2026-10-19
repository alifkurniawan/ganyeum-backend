package id.reshito.backend.modules.shipping;

import id.reshito.backend.common.dto.ListResponse;
import id.reshito.backend.common.exception.NotFoundException;
import id.reshito.backend.modules.shipping.dto.ShippingResponse;
import id.reshito.backend.modules.shipping.model.Shipping;
import id.reshito.backend.modules.shipping.repository.ShippingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ShippingService {
    private final ShippingRepository shippingRepository;

    @Transactional(readOnly = true)
    public ListResponse list() {
        var data = shippingRepository.findAllByOrderByIdAsc().stream()
                .map(shipping -> new ShippingResponse(
                        shipping.getId(), shipping.getName(), shipping.isRequiresTracking()))
                .toList();
        return new ListResponse(data, 1, 1, (long) data.size());
    }

    @Transactional(readOnly = true)
    public Shipping findById(Long id) {
        return shippingRepository.findById(id).orElseThrow(NotFoundException::new);
    }
}
