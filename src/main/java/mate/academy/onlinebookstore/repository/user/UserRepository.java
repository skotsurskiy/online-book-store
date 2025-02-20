package mate.academy.onlinebookstore.repository.user;

import java.util.Optional;
import mate.academy.onlinebookstore.model.ShoppingCart;
import mate.academy.onlinebookstore.model.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserRepository extends JpaRepository<User, Long> {
    @EntityGraph(attributePaths = "roles")
    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    @Query("""
                from ShoppingCart sc
                join fetch sc.user u
                left join fetch sc.cartItems ci
                where u.email = :email
            """)
    Optional<ShoppingCart> getShoppingCartByUser(@Param("email") String email);
}
