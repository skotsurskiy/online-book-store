package mate.academy.onlinebookstore.mapper;

import java.math.BigDecimal;
import java.util.Set;
import java.util.stream.Collectors;
import mate.academy.onlinebookstore.config.MapperConfig;
import mate.academy.onlinebookstore.dto.orderitem.OrderItemDto;
import mate.academy.onlinebookstore.model.CartItem;
import mate.academy.onlinebookstore.model.OrderItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(config = MapperConfig.class, uses = BookMapper.class)
public interface OrderItemMapper {
    @Mapping(target = "id", ignore = true)
    OrderItem toOrderItemFromCartItem(CartItem cartItems);

    @Mapping(target = "bookId", source = "book.id")
    OrderItemDto toOrderItemDto(OrderItem orderItem);

    @Named("setOrderItemsFromCartItems")
    default Set<OrderItem> setOrderItemsFromCartItems(
            Set<CartItem> cartItems
    ) {
        return cartItems.stream()
                .map(cartItem -> {
                    OrderItem orderItemFromCartItem = toOrderItemFromCartItem(cartItem);
                    orderItemFromCartItem.setPrice(cartItem.getBook().getPrice()
                            .multiply(BigDecimal.valueOf(cartItem.getQuantity())));
                    return orderItemFromCartItem;
                })
                .collect(Collectors.toSet());
    }

    @Named("setOrderItemDtosFromOrderItems")
    default Set<OrderItemDto> setOrderItemDtosFromOrderItems(Set<OrderItem> orderItems) {
        return orderItems.stream()
                .map(this::toOrderItemDto)
                .collect(Collectors.toSet());
    }
}
