package mate.academy.onlinebookstore.repository.shoppingcart;

import java.util.Optional;
import mate.academy.onlinebookstore.model.ShoppingCart;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ShoppingCartRepository extends JpaRepository<ShoppingCart, Long> {
    @EntityGraph(attributePaths = {
            "user",
            "cartItems",
            "cartItems.book"
    })
    Optional<ShoppingCart> getShoppingCartByUserId(Long id);
}
