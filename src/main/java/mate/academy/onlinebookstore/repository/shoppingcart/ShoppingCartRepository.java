package mate.academy.onlinebookstore.repository.shoppingcart;

import mate.academy.onlinebookstore.model.ShoppingCart;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ShoppingCartRepository extends JpaRepository<ShoppingCart, Long> {
}
