package mate.academy.onlinebookstore.repository;

import static mate.academy.onlinebookstore.util.TestUtil.teardown;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.Set;
import javax.sql.DataSource;
import mate.academy.onlinebookstore.model.ShoppingCart;
import mate.academy.onlinebookstore.model.User;
import mate.academy.onlinebookstore.repository.shoppingcart.ShoppingCartRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class ShoppingCartRepositoryTest {
    @Autowired
    private ShoppingCartRepository shoppingCartRepository;

    @BeforeAll
    static void beforeAll(@Autowired DataSource dataSource) {
        teardown(dataSource);
    }

    @Test
    @DisplayName("Get shopping cart by user id")
    @Sql(
            scripts = "classpath:database/shoppingCarts/add-user-with-role-and-shopping-cart.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
    )
    @Sql(
            scripts = "classpath:database/delete-all-from-tables.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD
    )
    void getShoppingCartByUserId_WhereUserIdIsOne_ReturnShoppingCart() {
        User user = new User();
        user.setId(1L);
        user.setEmail("email@email.com");
        user.setPassword("password");
        user.setFirstName("first");
        user.setLastName("last");
        user.setShippingAddress("address");

        ShoppingCart expected = new ShoppingCart();
        expected.setId(1L);
        expected.setUser(user);
        expected.setCartItems(Set.of());

        ShoppingCart actual = shoppingCartRepository
                .getShoppingCartByUserId(1L).orElse(null);

        assertThat(actual)
                .usingRecursiveComparison()
                .ignoringFields("user.roles")
                .isEqualTo(expected);
    }
}
