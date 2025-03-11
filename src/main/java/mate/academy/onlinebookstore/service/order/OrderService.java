package mate.academy.onlinebookstore.service.order;

import java.util.List;
import mate.academy.onlinebookstore.dto.order.OrderDto;
import mate.academy.onlinebookstore.dto.order.OrderRequestDto;
import mate.academy.onlinebookstore.dto.order.OrderStatusDto;
import mate.academy.onlinebookstore.dto.order.UpdateOrderStatusDto;
import mate.academy.onlinebookstore.dto.orderitem.OrderItemDto;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;

public interface OrderService {
    OrderDto completeOrder(OrderRequestDto orderRequestDto, Authentication authentication);

    List<OrderDto> getOrdersHistory(Pageable pageable, Authentication authentication);

    List<OrderItemDto> getOrderItemsByOrderId(Long orderId, Authentication authentication);

    OrderItemDto getOrderItemByOrderIdAndItemId(
            Long orderId,
            Long itemId,
            Authentication authentication
    );

    UpdateOrderStatusDto updateOrderStatus(Long orderId, OrderStatusDto orderStatusDto);
}
