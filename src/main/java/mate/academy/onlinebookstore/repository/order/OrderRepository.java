package mate.academy.onlinebookstore.repository.order;

import java.util.List;
import java.util.Optional;
import mate.academy.onlinebookstore.model.Order;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findAllByUserId(Long id, Pageable pageable);

    Optional<Order> findByIdAndUserId(Long id, Long userId);
}
