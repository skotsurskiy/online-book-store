package mate.academy.onlinebookstore.repository.cartitem;

import mate.academy.onlinebookstore.model.CartItem;
import mate.academy.onlinebookstore.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.security.core.Authentication;

import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    Optional<CartItem> findByIdAndShoppingCartUserEmail(Long id, String email);
}
