package mate.academy.onlinebookstore.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import mate.academy.onlinebookstore.dto.order.OrderDto;
import mate.academy.onlinebookstore.dto.order.OrderRequestDto;
import mate.academy.onlinebookstore.dto.order.UpdateOrderStatusDto;
import mate.academy.onlinebookstore.dto.orderitem.OrderItemDto;
import mate.academy.onlinebookstore.service.order.OrderService;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Order management", description = "Endpoints for managing order")
@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    @Operation(summary = "complete order", description = "complete order")
    @PreAuthorize("hasRole('USER')")
    @PostMapping
    public OrderDto completeOrder(
            @RequestBody @Valid OrderRequestDto orderRequestDto,
            Authentication authentication
    ) {
        return orderService.completeOrder(orderRequestDto, authentication);
    }

    @Operation(summary = "get orders history", description = "get orders history")
    @PreAuthorize("hasRole('USER')")
    @GetMapping
    public List<OrderDto> getOrdersHistory(Pageable pageable, Authentication authentication) {
        return orderService.getOrdersHistory(pageable, authentication);
    }

    @Operation(summary = "get order items", description = "get order items by id")
    @PreAuthorize("hasRole('USER')")
    @GetMapping("/{orderId}/items")
    public List<OrderItemDto> getOrderItemsByOrderId(
            @PathVariable Long orderId,
            Authentication authentication
    ) {
        return orderService.getOrderItemsByOrderId(orderId, authentication);
    }

    @Operation(
            summary = "get order item in order",
            description = "get order item by order id and item id"
    )
    @PreAuthorize("hasRole('USER')")
    @GetMapping("/{orderId}/items/{itemId}")
    public OrderItemDto getOrderItemByOrderIdAndItemId(
            @PathVariable Long orderId,
            @PathVariable Long itemId,
            Authentication authentication
    ) {
        return orderService.getOrderItemByOrderIdAndItemId(orderId, itemId, authentication);
    }

    @Operation(
            summary = "update order status",
            description = "update order status. Available order statuses: "
                    + "pending, completed, delivered"
    )
    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{orderId}")
    public UpdateOrderStatusDto updateOrderStatus(
            @PathVariable Long orderId,
            @RequestBody @Valid UpdateOrderStatusDto orderStatusDto
    ) {
        return orderService.updateOrderStatus(orderId, orderStatusDto);
    }
}
