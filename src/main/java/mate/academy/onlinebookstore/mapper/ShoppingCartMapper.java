package mate.academy.onlinebookstore.mapper;

import mate.academy.onlinebookstore.config.MapperConfig;
import mate.academy.onlinebookstore.dto.shoppingcart.ShoppingCartDto;
import mate.academy.onlinebookstore.model.ShoppingCart;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(config = MapperConfig.class, uses = {CartItemMapper.class, UserMapper.class})
public interface ShoppingCartMapper {
    @Mapping(target = "userId", source = "user.id")
    @Mapping(
            target = "cartItemResponseDtos",
            source = "cartItems",
            qualifiedByName = "getCartItemsDtos"
    )
    ShoppingCartDto toShoppingCartDto(ShoppingCart shoppingCart);
}
