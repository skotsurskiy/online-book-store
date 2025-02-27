package mate.academy.onlinebookstore.service.shoppingcart;

import mate.academy.onlinebookstore.dto.cartitem.CartItemRequestDto;
import mate.academy.onlinebookstore.dto.cartitem.CartItemUpdateQuantityDto;
import mate.academy.onlinebookstore.dto.shoppingcart.ShoppingCartDto;
import mate.academy.onlinebookstore.model.User;

public interface ShoppingCartService {
    ShoppingCartDto getShoppingCart();

    ShoppingCartDto addBookToShoppingCart(CartItemRequestDto requestDto);

    ShoppingCartDto updateCartItemQuantityById(
            Long id,
            CartItemUpdateQuantityDto updateQuantityDto
    );

    ShoppingCartDto deleteCartItemById(Long id);

    void createShoppingCartForUser(User user);

    void clear();
}
