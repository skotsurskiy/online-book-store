package mate.academy.onlinebookstore.util;

import java.math.BigDecimal;
import java.sql.Connection;
import java.util.List;
import java.util.Set;
import javax.sql.DataSource;
import lombok.SneakyThrows;
import mate.academy.onlinebookstore.dto.book.BookDto;
import mate.academy.onlinebookstore.dto.book.CreateBookRequestDto;
import mate.academy.onlinebookstore.dto.cartitem.CartItemRequestDto;
import mate.academy.onlinebookstore.dto.cartitem.CartItemResponseDto;
import mate.academy.onlinebookstore.dto.shoppingcart.ShoppingCartDto;
import mate.academy.onlinebookstore.model.Book;
import mate.academy.onlinebookstore.model.CartItem;
import mate.academy.onlinebookstore.model.Category;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ScriptUtils;

public class TestUtil {
    public static final String FIRST_BOOK_TITLE = "firstBook";
    public static final String SECOND_BOOK_TITLE = "secondBook";
    public static final String THIRD_BOOK_TITLE = "thirdBook";
    public static final String UPDATED_BOOK_TITLE = "Updated title";
    public static final String FIRST_CATEGORY_TITLE = "firstCategory";
    public static final String SECOND_CATEGORY_TITLE = "secondCategory";
    public static final String AUTHOR = "author";
    public static final BigDecimal PRICE = BigDecimal.valueOf(19.99);
    public static final String DESCRIPTION = "description";
    public static final String COVER_IMAGE = "coverImage";
    public static final String FIELD_ID = "id";
    public static final String BOOK_NOT_FOUND_ERROR_MESSAGE = "Can't find book by id: -1";
    public static final String CATEGORY_NOT_FOUND_ERROR_MESSAGE = "Can't find category by id: -1";
    public static final String CART_ITEM_NOT_FOUND_ERROR_MESSAGE = "Can't find cart item by id: -1";
    public static final String ISBN = "1111-2222-3333-4444";
    public static final String CATEGORY = "Category";
    public static final String URI_BOOKS = "/books";
    public static final String URI_BOOKS_ID = "/books/{id}";
    public static final String URI_BOOKS_SEARCH = "/books/search";
    public static final String URI_CATEGORIES = "/categories";
    public static final String URI_CATEGORIES_ID = "/categories/{id}";
    public static final String URI_CATEGORIES_ID_BOOKS = "/categories/{id}/books";
    public static final String URI_SHOPPING_CART = "/cart";
    public static final String URI_SHOPPING_CART_ID = "/cart/{id}";
    public static final String PARAM_TITLES = "titles";
    public static final long FIRST_VALID_ID = 1L;
    public static final long SECOND_VALID_ID = 2L;
    public static final long INVALID_ID = -1L;
    public static final int FIRST_LIST_INDEX = 0;
    public static final int SECOND_LIST_INDEX = 1;
    public static final int EXPECTED_LIST_SIZE = 2;

    @SneakyThrows
    public static void teardown(DataSource dataSource) {
        Connection connection = dataSource.getConnection();
        connection.setAutoCommit(true);
        ScriptUtils.executeSqlScript(
                connection,
                new ClassPathResource("database/delete-all-from-tables.sql")
        );
    }

    public static BookDto createBookDto(String title) {
        BookDto bookDto = new BookDto();
        bookDto.setTitle(title);
        bookDto.setAuthor(AUTHOR);
        bookDto.setPrice(PRICE);
        bookDto.setIsbn(ISBN);
        bookDto.setDescription(DESCRIPTION);
        bookDto.setCoverImage(COVER_IMAGE);
        bookDto.setCategoryIds(List.of());
        return bookDto;
    }

    public static BookDto createBookDto(Long id, String title) {
        BookDto bookDto = createBookDto(title);
        bookDto.setId(id);
        return bookDto;
    }

    public static Book createBook(Long id, String title) {
        Book book = new Book();
        book.setId(id);
        book.setTitle(title);
        book.setAuthor(AUTHOR);
        book.setPrice(PRICE);
        book.setIsbn(ISBN);
        book.setCategories(Set.of(createCategory(id)));
        return book;
    }

    public static CreateBookRequestDto createBookRequestDto(String title) {
        CreateBookRequestDto requestDto = new CreateBookRequestDto();
        requestDto.setTitle(title);
        requestDto.setAuthor(AUTHOR);
        requestDto.setPrice(PRICE);
        requestDto.setIsbn(ISBN);
        requestDto.setCategoryIds(List.of(FIRST_VALID_ID));
        requestDto.setDescription(DESCRIPTION);
        requestDto.setCoverImage(COVER_IMAGE);
        return requestDto;
    }

    public static Category createCategory(Long id) {
        Category category = new Category();
        category.setId(id);
        category.setName(CATEGORY);
        return category;
    }

    public static CartItem createCartItem(Book book) {
        CartItem cartItem = new CartItem();
        cartItem.setId(FIRST_VALID_ID);
        cartItem.setBook(book);
        cartItem.setQuantity(1);
        return cartItem;
    }

    public static ShoppingCartDto createShoppingCartDto(
            Set<CartItemResponseDto> cartItemResponseDtos
    ) {
        ShoppingCartDto shoppingCartDto = new ShoppingCartDto();
        shoppingCartDto.setId(FIRST_VALID_ID);
        shoppingCartDto.setUserId(FIRST_VALID_ID);
        shoppingCartDto.setCartItemResponseDtos(cartItemResponseDtos);
        return shoppingCartDto;
    }

    public static CartItemRequestDto createCartItemRequestDto(Long bookId) {
        CartItemRequestDto cartItemRequestDto = new CartItemRequestDto();
        cartItemRequestDto.setBookId(bookId);
        cartItemRequestDto.setQuantity(1);
        return cartItemRequestDto;
    }
}
