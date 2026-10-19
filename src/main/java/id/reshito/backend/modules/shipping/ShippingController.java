package id.reshito.backend.modules.shipping;

import id.reshito.backend.common.dto.ListResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/shipping")
@RequiredArgsConstructor
@PreAuthorize("hasRole('SELLER')")
public class ShippingController {
    private final ShippingService shippingService;

    @GetMapping
    public ListResponse list() {
        return shippingService.list();
    }
}
