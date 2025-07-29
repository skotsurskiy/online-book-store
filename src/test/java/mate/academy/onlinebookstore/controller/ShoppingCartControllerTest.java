package mate.academy.onlinebookstore.controller;

import static mate.academy.onlinebookstore.util.TestUtil.CART_ITEM_NOT_FOUND_ERROR_MESSAGE;
import static mate.academy.onlinebookstore.util.TestUtil.FIRST_BOOK_TITLE;
import static mate.academy.onlinebookstore.util.TestUtil.FIRST_VALID_ID;
import static mate.academy.onlinebookstore.util.TestUtil.INVALID_ID;
import static mate.academy.onlinebookstore.util.TestUtil.URI_SHOPPING_CART;
import static mate.academy.onlinebookstore.util.TestUtil.URI_SHOPPING_CART_ID;
import static mate.academy.onlinebookstore.util.TestUtil.createCartItemRequestDto;
import static mate.academy.onlinebookstore.util.TestUtil.createShoppingCartDto;
import static mate.academy.onlinebookstore.util.TestUtil.teardown;
import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Set;
import javax.sql.DataSource;
import mate.academy.onlinebookstore.dto.cartitem.CartItemRequestDto;
import mate.academy.onlinebookstore.dto.cartitem.CartItemResponseDto;
import mate.academy.onlinebookstore.dto.cartitem.CartItemUpdateQuantityDto;
import mate.academy.onlinebookstore.dto.shoppingcart.ShoppingCartDto;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ShoppingCartControllerTest {
    private static MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @BeforeAll
    static void beforeAll(
            @Autowired DataSource dataSource,
            @Autowired WebApplicationContext webApplicationContext
    ) throws SQLException {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(webApplicationContext)
                .apply(SecurityMockMvcConfigurers.springSecurity())
                .build();
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(true);
            ScriptUtils.executeSqlScript(
                    connection,
                    new ClassPathResource("database/delete-all-from-tables.sql")
            );
        }
    }

    @AfterAll
    static void afterAll(@Autowired DataSource dataSource) {
        teardown(dataSource);
    }

    @Test
    @WithUserDetails(
            value = "email@email.com",
            userDetailsServiceBeanName = "customUserDetailsService"
    )
    @DisplayName("Get shopping cart by user")
    @Sql(
            scripts = {
                    "classpath:database/shoppingCarts/add-user-with-role-and-shopping-cart.sql",
                    "classpath:database/books/add-one-book-to-books-table.sql",
                    "classpath:database/cartItems/add-one-cart-item.sql"
            },
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
    )
    @Sql(
            scripts = {
                    "classpath:database/delete-all-from-tables.sql"
            },
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD
    )
    void getShoppingCart_ReturnShoppingCartDto() throws Exception {
        CartItemResponseDto cartItemResponseDto = new CartItemResponseDto(
                FIRST_VALID_ID,
                FIRST_VALID_ID,
                FIRST_BOOK_TITLE,
                1
        );
        ShoppingCartDto expected = createShoppingCartDto(Set.of(cartItemResponseDto));

        MvcResult mvcResult = mockMvc.perform(
                        MockMvcRequestBuilders
                                .get(URI_SHOPPING_CART)
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        readValueAndTest(expected, mvcResult);
    }

    @Test
    @WithUserDetails(
            value = "email@email.com",
            userDetailsServiceBeanName = "customUserDetailsService"
    )
    @DisplayName("add book to shopping cart with cartItemRequestDto, return shoppingCartDto")
    @Sql(
            scripts = {
                    "classpath:database/cartItems/truncate-cart-items-table.sql",
                    "classpath:database/shoppingCarts/add-user-with-role-and-shopping-cart.sql",
                    "classpath:database/books/add-one-book-to-books-table.sql"
            },
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
    )
    @Sql(
            scripts = {
                    "classpath:database/delete-all-from-tables.sql"
            },
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD
    )
    void addBookToShoppingCart_WithCartItemRequestDto_ReturnShoppingCartDto() throws Exception {
        CartItemResponseDto cartItemResponseDto = new CartItemResponseDto(
                FIRST_VALID_ID,
                FIRST_VALID_ID,
                FIRST_BOOK_TITLE,
                1
        );
        ShoppingCartDto expected = createShoppingCartDto(Set.of(cartItemResponseDto));
        CartItemRequestDto requestDto = createCartItemRequestDto(FIRST_VALID_ID);

        MvcResult mvcResult = mockMvc.perform(
                        MockMvcRequestBuilders
                                .post(URI_SHOPPING_CART)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        readValueAndTest(expected, mvcResult);
    }

    @Test
    @WithUserDetails(
            value = "email@email.com",
            userDetailsServiceBeanName = "customUserDetailsService"
    )
    @DisplayName("update book quantity with valid id, returns ShoppingCartDto")
    @Sql(
            scripts = {
                    "classpath:database/shoppingCarts/add-user-with-role-and-shopping-cart.sql",
                    "classpath:database/books/add-one-book-to-books-table.sql",
                    "classpath:database/cartItems/add-one-cart-item.sql"
            },
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
    )
    @Sql(
            scripts = {
                    "classpath:database/delete-all-from-tables.sql"
            },
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD
    )
    void updateCartItemQuantityById_WithValidId_ReturnShoppingCartDto() throws Exception {
        CartItemResponseDto cartItemResponseDto = new CartItemResponseDto(
                FIRST_VALID_ID,
                FIRST_VALID_ID,
                FIRST_BOOK_TITLE,
                2
        );
        ShoppingCartDto expected = createShoppingCartDto(Set.of(cartItemResponseDto));

        CartItemUpdateQuantityDto updateQuantityDto = new CartItemUpdateQuantityDto(2);

        MvcResult mvcResult = mockMvc.perform(
                        MockMvcRequestBuilders
                                .put(URI_SHOPPING_CART_ID, FIRST_VALID_ID)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(updateQuantityDto)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        readValueAndTest(expected, mvcResult);
    }

    @Test
    @WithUserDetails(
            value = "email@email.com",
            userDetailsServiceBeanName = "customUserDetailsService"
    )
    @DisplayName("update book quantity with invalid id, throw EntityNotFoundException")
    @Sql(
            scripts = {
                    "classpath:database/shoppingCarts/add-user-with-role-and-shopping-cart.sql",
                    "classpath:database/books/add-one-book-to-books-table.sql",
                    "classpath:database/cartItems/add-one-cart-item.sql"
            },
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
    )
    @Sql(
            scripts = {
                    "classpath:database/delete-all-from-tables.sql"
            },
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD
    )
    void updateCartItemQuantityById_WithInvalidId_throwEntityNotFoundException() throws Exception {
        CartItemUpdateQuantityDto updateQuantityDto = new CartItemUpdateQuantityDto(2);

        MvcResult mvcResult = mockMvc.perform(
                        MockMvcRequestBuilders
                                .put(URI_SHOPPING_CART_ID, INVALID_ID)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(updateQuantityDto)))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andReturn();

        String errorMessage = mvcResult.getResponse().getContentAsString();

        assertThat(errorMessage).contains(CART_ITEM_NOT_FOUND_ERROR_MESSAGE);
    }

    @Test
    @WithUserDetails(
            value = "email@email.com",
            userDetailsServiceBeanName = "customUserDetailsService"
    )
    @DisplayName("delete cart item with valid id, return ShoppingCartDto")
    @Sql(
            scripts = {
                    "classpath:database/shoppingCarts/add-user-with-role-and-shopping-cart.sql",
                    "classpath:database/books/add-one-book-to-books-table.sql",
                    "classpath:database/cartItems/add-one-cart-item.sql"
            },
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
    )
    @Sql(
            scripts = {
                    "classpath:database/delete-all-from-tables.sql"
            },
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD
    )
    void deleteCartItem_WithValidId_ReturnShoppingCartDto() throws Exception {
        ShoppingCartDto expected = createShoppingCartDto(Set.of());

        MvcResult mvcResult = mockMvc.perform(
                        MockMvcRequestBuilders
                                .delete(URI_SHOPPING_CART_ID, FIRST_VALID_ID)
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNoContent())
                .andReturn();

        readValueAndTest(expected, mvcResult);
    }

    private void readValueAndTest(ShoppingCartDto expected, MvcResult mvcResult) throws Exception {
        ShoppingCartDto actual = objectMapper.readValue(
                mvcResult.getResponse().getContentAsString(),
                ShoppingCartDto.class
        );

        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }
}
