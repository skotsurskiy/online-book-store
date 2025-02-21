package mate.academy.onlinebookstore.dto.shoppingcart;

import java.util.Set;
import lombok.Getter;
import lombok.Setter;
import mate.academy.onlinebookstore.dto.cartitem.CartItemResponseDto;

@Getter
@Setter
public class ShoppingCartDto {
    private Long id;
    private Long userId;
    private Set<CartItemResponseDto> cartItemResponseDtos;
}
