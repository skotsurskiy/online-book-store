package mate.academy.onlinebookstore.repository.shoppingcart;

import java.util.Optional;
import mate.academy.onlinebookstore.model.ShoppingCart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ShoppingCartRepository extends JpaRepository<ShoppingCart, Long> {
    @Query("""
                from ShoppingCart sc
                join fetch sc.user u
                left join fetch sc.cartItems ci
                left join fetch ci.book
                where u.id = :id
            """)
    Optional<ShoppingCart> getShoppingCartByUserId(@Param("id") Long id);
}
