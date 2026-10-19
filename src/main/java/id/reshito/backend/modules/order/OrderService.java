package id.reshito.backend.modules.order;

import id.reshito.backend.common.exception.NotFoundException;
import id.reshito.backend.common.dto.ListResponse;
import id.reshito.backend.modules.customer.CustomerService;
import id.reshito.backend.modules.order.dto.CreateOrderRequest;
import id.reshito.backend.modules.order.dto.PurchaseOrderResponse;
import id.reshito.backend.modules.order.dto.UpdateOrderStatusRequest;
import id.reshito.backend.modules.order.model.OrderStatus;
import id.reshito.backend.modules.order.model.PurchaseOrder;
import id.reshito.backend.modules.order.model.PurchaseOrderDetail;
import id.reshito.backend.modules.order.model.ShippingDetail;
import id.reshito.backend.modules.order.repository.PurchaseOrderRepository;
import id.reshito.backend.modules.shipping.ShippingService;
import id.reshito.backend.modules.product.ProductRepository;
import id.reshito.backend.modules.product.model.Product;
import id.reshito.backend.modules.user.User;
import id.reshito.backend.modules.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigInteger;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {
    private static final DateTimeFormatter ID_DATE = DateTimeFormatter.ofPattern("yyMMdd").withZone(ZoneOffset.UTC);
    private final UserService userService;
    private final CustomerService customerService;
    private final ProductRepository productRepository;
    private final ShippingService shippingService;
    private final PurchaseOrderRepository purchaseOrderRepository;

    @Transactional
    public PurchaseOrderResponse create(String sellerUid, CreateOrderRequest request) {

        User seller = userService.findByUid(sellerUid);
        User customer = customerService.resolveBuyer(seller, request.customerId(),
                request.customerName(),
                request.customerPhoneNumber()
        );
        String phone = userService.normalizePhoneNumber(request.customerPhoneNumber());

        if ((request.shippingId() == null) != (request.shippingCost() == null)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "shippingId and shippingCost must both be provided for delivery, or both omitted for pickup");
        }
        var shipping = request.shippingId() == null ? null
                : shippingService.findById(request.shippingId());
        String trackingNumber = blankToNull(request.trackingNumber());
        if (shipping == null && trackingNumber != null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "trackingNumber requires a shipping method");
        }
        if (shipping != null && shipping.isRequiresTracking() && trackingNumber == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "trackingNumber is required for this shipping method");
        }
        if (shipping != null && !shipping.isRequiresTracking() && trackingNumber != null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "trackingNumber is not applicable for this shipping method");
        }
        BigInteger shippingCost = request.shippingCost() == null ? BigInteger.ZERO : request.shippingCost();
        List<UUID> productIds = request.items().stream().map(CreateOrderRequest.Item::productId).distinct().toList();
        if (productIds.size() != request.items().size()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Each product may appear only once");
        }
        Map<UUID, Product> products = productRepository.findAllByIdInAndUidAndActive(productIds, sellerUid, true)
                .stream().collect(Collectors.toMap(Product::getId, product -> product));
        if (products.size() != productIds.size()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "One or more products are missing or not owned by this seller");
        }

        Instant issuedAt = Instant.now();
        BigInteger subtotal = BigInteger.ZERO;
        BigInteger totalInitialCost = BigInteger.ZERO;
        List<PurchaseOrderDetail> details = new ArrayList<>();
        for (CreateOrderRequest.Item item : request.items()) {
            Product product = products.get(item.productId());
            if (product.getSellingPrice() == null || product.getSellingPrice().signum() < 0) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Product has an invalid selling price");
            }
            if (product.getInitialPrice() == null || product.getInitialPrice().signum() < 0) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Product has an invalid initial cost");
            }
            BigInteger lineTotal = product.getSellingPrice().multiply(BigInteger.valueOf(item.quantity()));
            BigInteger lineInitialCost = product.getInitialPrice().multiply(BigInteger.valueOf(item.quantity()));
            subtotal = subtotal.add(lineTotal);
            totalInitialCost = totalInitialCost.add(lineInitialCost);
            details.add(PurchaseOrderDetail.builder().product(product).productName(product.getProductName())
                    .pricePerItem(product.getSellingPrice()).initialCostPerItem(product.getInitialPrice())
                    .quantity(item.quantity()).totalPrice(lineTotal).totalInitialCost(lineInitialCost).build());
        }
        BigInteger total = subtotal.add(shippingCost);
        String code = UUID.randomUUID().toString().replace("-", "").substring(0, 6).toUpperCase(Locale.ROOT);
        PurchaseOrder order = PurchaseOrder.builder()
                .orderId("PO-" + ID_DATE.format(issuedAt) + "-" + code)
                .invoiceId("INV-" + ID_DATE.format(issuedAt) + "-" + code)
                .seller(seller).customer(customer)
                .customerName(request.customerName().trim()).customerPhoneNumber(phone)
                .customerAddress(request.customerAddress().trim())
                .products(details).totalPrice(total).totalInitialCost(totalInitialCost)
                .profit(subtotal.subtract(totalInitialCost)).shipping(shipping)
                .shippingName(shipping == null ? null : shipping.getName())
                .invoiceIssuedDate(issuedAt).build();
        if (shipping != null) {
            order.setShippingDetail(ShippingDetail.builder().purchaseOrder(order).shippingCost(shippingCost)
                    .trackingNumber(trackingNumber).build());
        }
        details.forEach(detail -> detail.setPurchaseOrder(order));
        purchaseOrderRepository.save(order);
        return toResponse(order, subtotal);
    }

    @Transactional(readOnly = true)
    public ListResponse list(String sellerUid, OrderStatus status, LocalDate fromDate, LocalDate toDate,
                             String orderId, String invoiceId, int page, int size) {
        if (fromDate != null && toDate != null && toDate.isBefore(fromDate)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "toDate must be on or after fromDate");
        }
        User seller = userService.findByUid(sellerUid);
        Instant from = fromDate == null ? null : fromDate.atStartOfDay(ZoneOffset.UTC).toInstant();
        Instant until = toDate == null ? null : toDate.plusDays(1).atStartOfDay(ZoneOffset.UTC).toInstant();
        var results = purchaseOrderRepository.searchForSeller(seller.getId(), status, from, until,
                blankToNull(orderId), blankToNull(invoiceId),
                PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "invoiceIssuedDate")));
        var data = results.getContent().stream()
                .map(order -> toResponse(order, calculateSubtotal(order)))
                .toList();
        return new ListResponse(data, page, results.getTotalPages(), results.getTotalElements());
    }

    @Transactional
    public PurchaseOrderResponse updateStatus(String sellerUid, UUID orderUuid, OrderStatus status) {
        User seller = userService.findByUid(sellerUid);
        PurchaseOrder order = purchaseOrderRepository.findByIdAndSeller_Id(orderUuid, seller.getId())
                .orElseThrow(NotFoundException::new);
        order.setStatus(status);
        return toResponse(order, calculateSubtotal(order));
    }

    @Transactional(readOnly = true)
    public PurchaseOrderResponse getDetail(String sellerUid, UUID orderUuid) {
        User seller = userService.findByUid(sellerUid);
        PurchaseOrder order = purchaseOrderRepository.findByIdAndSeller_Id(orderUuid, seller.getId())
                .orElseThrow(NotFoundException::new);
        return toResponse(order, calculateSubtotal(order));
    }

    private static BigInteger calculateSubtotal(PurchaseOrder order) {
        return order.getProducts().stream().map(PurchaseOrderDetail::getTotalPrice)
                .reduce(BigInteger.ZERO, BigInteger::add);
    }

    private static String blankToNull(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }

    private static PurchaseOrderResponse toResponse(PurchaseOrder order, BigInteger subtotal) {
        var items = order.getProducts().stream().map(detail -> new PurchaseOrderResponse.Item(
                detail.getProduct().getId(), detail.getProductName(), detail.getPricePerItem(),
                detail.getInitialCostPerItem(), detail.getQuantity(), detail.getTotalPrice(),
                detail.getTotalInitialCost())).toList();
        return new PurchaseOrderResponse(order.getId(), order.getOrderId(), order.getStatus(), order.getCustomer().getId(),
                order.getCustomerName(), order.getCustomerPhoneNumber(), order.getCustomerAddress(), items,
                subtotal, order.getTotalInitialCost(), order.getProfit(),
                order.getShippingDetail() == null ? BigInteger.ZERO : order.getShippingDetail().getShippingCost(),
                order.getTotalPrice(), order.getShipping() == null ? null : order.getShipping().getId(),
                order.getShippingName(), order.getShippingDetail() == null ? null
                : order.getShippingDetail().getTrackingNumber(), order.getInvoiceId(), order.getInvoiceIssuedDate());
    }
}
