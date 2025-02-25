package mate.academy.onlinebookstore.service.shoppingcart;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import mate.academy.onlinebookstore.dto.cartitem.CartItemRequestDto;
import mate.academy.onlinebookstore.dto.cartitem.CartItemUpdateQuantityDto;
import mate.academy.onlinebookstore.dto.shoppingcart.ShoppingCartDto;
import mate.academy.onlinebookstore.exception.EntityNotFoundException;
import mate.academy.onlinebookstore.mapper.CartItemMapper;
import mate.academy.onlinebookstore.mapper.ShoppingCartMapper;
import mate.academy.onlinebookstore.model.CartItem;
import mate.academy.onlinebookstore.model.ShoppingCart;
import mate.academy.onlinebookstore.model.User;
import mate.academy.onlinebookstore.repository.book.BookRepository;
import mate.academy.onlinebookstore.repository.cartitem.CartItemRepository;
import mate.academy.onlinebookstore.repository.shoppingcart.ShoppingCartRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
public class ShoppingCartServiceImpl implements ShoppingCartService {
    private final ShoppingCartMapper shoppingCartMapper;
    private final ShoppingCartRepository shoppingCartRepository;
    private final CartItemMapper cartItemMapper;
    private final CartItemRepository cartItemRepository;
    private final BookRepository bookRepository;

    @Override
    public ShoppingCartDto getShoppingCart() {
        return shoppingCartMapper.toShoppingCartDto(getUserShoppingCart());
    }

    @Override
    public ShoppingCartDto addBookToShoppingCart(CartItemRequestDto requestDto) {
        ShoppingCart shoppingCart = getUserShoppingCart();
        CartItem cartItem = cartItemMapper.toCartItemEntity(requestDto);
        cartItem.setShoppingCart(shoppingCart);
        cartItem.setBook(bookRepository.findBookById(requestDto.getBookId())
                .orElseThrow(() -> new EntityNotFoundException("Can't find book with id: "
                        + requestDto.getBookId())));
        shoppingCart.getCartItems().add(cartItem);
        shoppingCartRepository.save(shoppingCart);
        return shoppingCartMapper.toShoppingCartDto(shoppingCart);
    }

    @Override
    public ShoppingCartDto updateCartItemQuantityById(
            Long id,
            CartItemUpdateQuantityDto updateQuantityDto
    ) {
        CartItem cartItemEntity = findCartItemById(id);
        cartItemEntity.setQuantity(updateQuantityDto.getQuantity());
        cartItemRepository.save(cartItemEntity);

        return shoppingCartMapper.toShoppingCartDto(getUserShoppingCart());
    }

    @Override
    public ShoppingCartDto deleteCartItemById(Long id) {
        CartItem cartItemEntity = findCartItemById(id);
        cartItemRepository.delete(cartItemEntity);
        return getShoppingCart();
    }

    @Override
    public void createShoppingCartForUser(User user) {
        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.setUser(user);
        shoppingCartRepository.save(shoppingCart);
    }

    private ShoppingCart getUserShoppingCart() {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return shoppingCartRepository.getShoppingCartByUserId(user.getId())
                .orElseThrow(() -> new EntityNotFoundException("Can't find shopping cart"));
    }

    private CartItem findCartItemById(Long id) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return cartItemRepository.findByIdAndShoppingCartId(id, user.getId())
                .orElseThrow(()
                        -> new EntityNotFoundException("Can't find cart item by id: " + id));
    }
}
