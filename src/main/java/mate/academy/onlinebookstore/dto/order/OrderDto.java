package mate.academy.onlinebookstore.dto.order;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;
import mate.academy.onlinebookstore.dto.orderitem.OrderItemDto;
import mate.academy.onlinebookstore.model.Order;

@Getter
@Setter
public class OrderDto {
    private Long id;
    private Long userId;
    private String shippingAddress;
    private Set<OrderItemDto> orderItemDtos;
    private LocalDateTime orderDate;
    private BigDecimal total;
    private Order.Status status;
}
