package mate.academy.onlinebookstore.service;

import static mate.academy.onlinebookstore.util.TestUtil.BOOK_NOT_FOUND_ERROR_MESSAGE;
import static mate.academy.onlinebookstore.util.TestUtil.CART_ITEM_NOT_FOUND_ERROR_MESSAGE;
import static mate.academy.onlinebookstore.util.TestUtil.FIRST_BOOK_TITLE;
import static mate.academy.onlinebookstore.util.TestUtil.FIRST_VALID_ID;
import static mate.academy.onlinebookstore.util.TestUtil.INVALID_ID;
import static mate.academy.onlinebookstore.util.TestUtil.createBook;
import static mate.academy.onlinebookstore.util.TestUtil.createCartItem;
import static mate.academy.onlinebookstore.util.TestUtil.createCartItemRequestDto;
import static mate.academy.onlinebookstore.util.TestUtil.createShoppingCartDto;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import jakarta.persistence.EntityNotFoundException;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import mate.academy.onlinebookstore.dto.cartitem.CartItemRequestDto;
import mate.academy.onlinebookstore.dto.cartitem.CartItemResponseDto;
import mate.academy.onlinebookstore.dto.cartitem.CartItemUpdateQuantityDto;
import mate.academy.onlinebookstore.dto.shoppingcart.ShoppingCartDto;
import mate.academy.onlinebookstore.mapper.BookMapper;
import mate.academy.onlinebookstore.mapper.CartItemMapper;
import mate.academy.onlinebookstore.mapper.ShoppingCartMapper;
import mate.academy.onlinebookstore.mapper.impl.BookMapperImpl;
import mate.academy.onlinebookstore.mapper.impl.CartItemMapperImpl;
import mate.academy.onlinebookstore.mapper.impl.ShoppingCartMapperImpl;
import mate.academy.onlinebookstore.model.Book;
import mate.academy.onlinebookstore.model.CartItem;
import mate.academy.onlinebookstore.model.ShoppingCart;
import mate.academy.onlinebookstore.model.User;
import mate.academy.onlinebookstore.repository.book.BookRepository;
import mate.academy.onlinebookstore.repository.cartitem.CartItemRepository;
import mate.academy.onlinebookstore.repository.shoppingcart.ShoppingCartRepository;
import mate.academy.onlinebookstore.service.shoppingcart.ShoppingCartServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

@ExtendWith(MockitoExtension.class)
class ShoppingCartServiceImplTest {
    @InjectMocks
    private ShoppingCartServiceImpl shoppingCartService;
    @Mock
    private ShoppingCartRepository shoppingCartRepository;
    @Mock
    private BookRepository bookRepository;
    @Mock
    private CartItemRepository cartItemRepository;
    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private SecurityContext securityContext;
    @Spy
    private BookMapper bookMapper = new BookMapperImpl();
    @Spy
    private CartItemMapper cartItemMapper = new CartItemMapperImpl(bookMapper);
    @Spy
    private ShoppingCartMapper shoppingCartMapper = new ShoppingCartMapperImpl(cartItemMapper);

    @Test
    @DisplayName("add book to shopping cart with valid cartItem, return shoppingCartDto")
    void addBookToShoppingCart_WithValidCartItemRequestDto_ReturnShoppingCartDto() {
        User user = createUserAndMockSecurityContext();
        Book book = createBook(FIRST_VALID_ID, FIRST_BOOK_TITLE);
        ShoppingCart shoppingCart = createShoppingCart(user, new HashSet<>());
        CartItemRequestDto cartItemRequestDto = createCartItemRequestDto(FIRST_VALID_ID);

        ShoppingCartDto expected = createShoppingCartDto(Set.of(
                new CartItemResponseDto(null, FIRST_VALID_ID, FIRST_BOOK_TITLE, 1))
        );

        when(shoppingCartRepository.getShoppingCartByUserId(user.getId()))
                .thenReturn(Optional.of(shoppingCart));
        when(bookRepository.findBookById(cartItemRequestDto.getBookId()))
                .thenReturn(Optional.of(book));
        when(shoppingCartRepository.save(shoppingCart)).thenReturn(shoppingCart);

        ShoppingCartDto actual = shoppingCartService.addBookToShoppingCart(cartItemRequestDto);

        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
        verify(shoppingCartRepository).getShoppingCartByUserId(user.getId());
        verify(bookRepository).findBookById(cartItemRequestDto.getBookId());
        verify(shoppingCartRepository).save(shoppingCart);
        verifyNoMoreInteractions(shoppingCartRepository, bookRepository);
    }

    @Test
    @DisplayName("add book to shopping cart with invalid bookId, throw EntityNotFoundException")
    void addBookToShoppingCart_WithInvalidBookId_ThrowEntityNotFoundException() {
        User user = createUserAndMockSecurityContext();
        ShoppingCart shoppingCart = createShoppingCart(user, Set.of());
        CartItemRequestDto cartItemRequestDto = createCartItemRequestDto(INVALID_ID);

        when(shoppingCartRepository.getShoppingCartByUserId(user.getId()))
                .thenReturn(Optional.of(shoppingCart));
        when(bookRepository.findBookById(cartItemRequestDto.getBookId()))
                .thenReturn(Optional.empty());

        Exception exception = assertThrows(
                EntityNotFoundException.class,
                () -> shoppingCartService.addBookToShoppingCart(cartItemRequestDto)
        );

        Assertions.assertEquals(BOOK_NOT_FOUND_ERROR_MESSAGE, exception.getMessage());
    }

    @Test
    @DisplayName("update cart item quantity in shopping cart with valid id, return ShoppingCartDto")
    void updateCartItemQuantityById_WithValidId_ReturnShoppingCartDto() {
        User user = createUserAndMockSecurityContext();
        Book book = createBook(FIRST_VALID_ID, FIRST_BOOK_TITLE);
        CartItem cartItem = createCartItem(book);
        ShoppingCart shoppingCart = createShoppingCart(user, Set.of(cartItem));

        CartItemUpdateQuantityDto updateQuantityDto = new CartItemUpdateQuantityDto(2);

        ShoppingCartDto expected = createShoppingCartDto(Set.of(
                new CartItemResponseDto(
                        FIRST_VALID_ID,
                        FIRST_VALID_ID,
                        FIRST_BOOK_TITLE, 2)
                )
        );

        when(cartItemRepository.findByIdAndShoppingCartId(FIRST_VALID_ID, user.getId()))
                .thenReturn(Optional.of(cartItem));
        when(cartItemRepository.save(cartItem))
                .thenReturn(cartItem);
        when(shoppingCartRepository.getShoppingCartByUserId(user.getId()))
                .then(invocationOnMock -> {
                    shoppingCart.getCartItems().forEach(item
                            -> item.setQuantity(updateQuantityDto.quantity())
                    );
                    return Optional.of(shoppingCart);
                });

        ShoppingCartDto actual = shoppingCartService
                .updateCartItemQuantityById(FIRST_VALID_ID, updateQuantityDto);

        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
        verify(cartItemRepository).findByIdAndShoppingCartId(FIRST_VALID_ID, user.getId());
        verify(shoppingCartRepository).getShoppingCartByUserId(user.getId());
        verify(cartItemRepository).save(cartItem);
        verifyNoMoreInteractions(cartItemRepository, shoppingCartRepository);
    }

    @Test
    @DisplayName("update cart item quantity in shopping cart with invalid id, "
            + "throw EntityNotFoundException"
    )
    void updateCartItemQuantityById_WithInvalidId_ThrowEntityNotFoundException() {
        User user = createUserAndMockSecurityContext();

        when(cartItemRepository.findByIdAndShoppingCartId(INVALID_ID, user.getId()))
                .thenReturn(Optional.empty());

        Exception exception = assertThrows(EntityNotFoundException.class, () -> shoppingCartService
                .updateCartItemQuantityById(INVALID_ID, new CartItemUpdateQuantityDto(1)));

        assertEquals(CART_ITEM_NOT_FOUND_ERROR_MESSAGE, exception.getMessage());
    }

    private User createUserAndMockSecurityContext() {
        User user = new User();
        user.setId(FIRST_VALID_ID);

        SecurityContextHolder.setContext(securityContext);
        when(SecurityContextHolder.getContext().getAuthentication().getPrincipal())
                .thenReturn(user);

        return user;
    }

    private ShoppingCart createShoppingCart(User user, Set<CartItem> cartItems) {
        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.setId(FIRST_VALID_ID);
        shoppingCart.setUser(user);
        shoppingCart.setCartItems(cartItems);
        return shoppingCart;
    }
}
