package mate.academy.onlinebookstore.service.order;

import java.util.List;
import mate.academy.onlinebookstore.dto.order.OrderDto;
import mate.academy.onlinebookstore.dto.order.OrderRequestDto;
import mate.academy.onlinebookstore.dto.order.OrderStatusDto;
import mate.academy.onlinebookstore.dto.orderitem.OrderItemDto;
import org.springframework.data.domain.Pageable;

public interface OrderService {
    OrderDto completeOrder(OrderRequestDto orderRequestDto);

    List<OrderDto> getOrdersHistory(Pageable pageable);

    List<OrderItemDto> getOrderItemsByOrderId(Long orderId);

    OrderItemDto getOrderItemByOrderIdAndItemId(Long orderId, Long itemId);

    OrderStatusDto updateOrderStatus(Long orderId, OrderStatusDto orderStatusDto);
}
