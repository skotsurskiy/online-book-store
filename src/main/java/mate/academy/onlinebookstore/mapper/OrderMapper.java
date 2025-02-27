package mate.academy.onlinebookstore.mapper;

import java.time.LocalDateTime;
import mate.academy.onlinebookstore.config.MapperConfig;
import mate.academy.onlinebookstore.dto.order.OrderDto;
import mate.academy.onlinebookstore.model.Order;
import mate.academy.onlinebookstore.model.ShoppingCart;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(config = MapperConfig.class, uses = {UserMapper.class, OrderItemMapper.class})
public interface OrderMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(
            target = "orderItems",
            source = "cartItems",
            qualifiedByName = "setOrderItemsFromCartItems"
    )
    Order toOrderEntityFromShoppingCart(ShoppingCart shoppingCart);

    @Mapping(target = "userId", source = "user.id")
    @Mapping(
            target = "orderItemDtos",
            source = "orderItems",
            qualifiedByName = "setOrderItemDtosFromOrderItems"
    )
    OrderDto toOrderDto(Order order);

    @AfterMapping
    default void setStatusAndOrderDate(@MappingTarget Order order) {
        order.setStatus(Order.Status.PENDING);
        order.setOrderDate(LocalDateTime.now());
    }
}
