package mate.academy.onlinebookstore.mapper;

import java.util.Set;
import java.util.stream.Collectors;
import mate.academy.onlinebookstore.config.MapperConfig;
import mate.academy.onlinebookstore.dto.cartitem.CartItemRequestDto;
import mate.academy.onlinebookstore.dto.cartitem.CartItemResponseDto;
import mate.academy.onlinebookstore.model.CartItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(config = MapperConfig.class, uses = BookMapper.class)
public interface CartItemMapper {
    @Mapping(target = "bookId", source = "book.id")
    @Mapping(target = "bookTitle", source = "book.title")
    CartItemResponseDto toCartItemResponseDto(CartItem cartItem);

    @Mapping(target = "book", source = "bookId", qualifiedByName = "getBookById")
    CartItem toCartItemEntity(CartItemRequestDto requestDto);

    @Named("getCartItemsDtos")
    default Set<CartItemResponseDto> getCartItemsDtos(Set<CartItem> cartItems) {
        return cartItems.stream()
                .map(this::toCartItemResponseDto)
                .collect(Collectors.toSet());
    }
}
