package mate.academy.onlinebookstore.mapper;

import mate.academy.onlinebookstore.config.MapperConfig;
import mate.academy.onlinebookstore.dto.order.OrderDto;
import mate.academy.onlinebookstore.model.Order;
import mate.academy.onlinebookstore.model.ShoppingCart;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

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
}
