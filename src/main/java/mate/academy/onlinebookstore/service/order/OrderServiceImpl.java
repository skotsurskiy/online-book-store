package mate.academy.onlinebookstore.service.order;

import jakarta.transaction.Transactional;
import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import mate.academy.onlinebookstore.dto.order.OrderDto;
import mate.academy.onlinebookstore.dto.order.OrderRequestDto;
import mate.academy.onlinebookstore.dto.order.OrderStatusDto;
import mate.academy.onlinebookstore.dto.orderitem.OrderItemDto;
import mate.academy.onlinebookstore.exception.EmptyCartException;
import mate.academy.onlinebookstore.exception.EntityNotFoundException;
import mate.academy.onlinebookstore.exception.InvalidOrderStatusException;
import mate.academy.onlinebookstore.mapper.OrderItemMapper;
import mate.academy.onlinebookstore.mapper.OrderMapper;
import mate.academy.onlinebookstore.model.Order;
import mate.academy.onlinebookstore.model.OrderItem;
import mate.academy.onlinebookstore.model.ShoppingCart;
import mate.academy.onlinebookstore.model.User;
import mate.academy.onlinebookstore.repository.order.OrderRepository;
import mate.academy.onlinebookstore.repository.shoppingcart.ShoppingCartRepository;
import mate.academy.onlinebookstore.service.shoppingcart.ShoppingCartService;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderServiceImpl implements OrderService {
    private final ShoppingCartService shoppingCartService;
    private final OrderMapper orderMapper;
    private final OrderRepository orderRepository;
    private final ShoppingCartRepository shoppingCartRepository;
    private final OrderItemMapper orderItemMapper;

    @Override
    public OrderDto completeOrder(OrderRequestDto orderRequestDto) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        ShoppingCart shoppingCart = shoppingCartRepository.getShoppingCartByUserId(user.getId())
                .orElseThrow(()
                        -> new EntityNotFoundException("Can't find shopping cart by id: "
                        + user.getId()));
        if (shoppingCart.getCartItems().isEmpty()) {
            throw new EmptyCartException("Shopping cart is empty. "
                    + "Add items before placing an order.");
        }
        Order order = createOrder(shoppingCart, orderRequestDto);
        shoppingCartService.clear();
        return orderMapper.toOrderDto(orderRepository.save(order));
    }

    @Override
    public List<OrderDto> getOrdersHistory(Pageable pageable) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return orderRepository.findAllByUserId(user.getId(), pageable).stream()
                .map(orderMapper::toOrderDto)
                .toList();
    }

    @Override
    public List<OrderItemDto> getOrderItemsByOrderId(Long orderId) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Order order = orderRepository.findByIdAndUserId(orderId, user.getId()).orElseThrow(()
                -> new EntityNotFoundException("Can't find order by id: " + orderId));
        return order.getOrderItems().stream()
                .map(orderItemMapper::toOrderItemDto)
                .toList();
    }

    @Override
    public OrderItemDto getOrderItemByOrderIdAndItemId(Long orderId, Long itemId) {
        return getOrderItemsByOrderId(orderId).stream()
                .filter(orderItemDto -> orderItemDto.id().equals(itemId))
                .findFirst()
                .orElseThrow(()
                        -> new EntityNotFoundException("Can't find order by orderId: %s, itemId: %s"
                        .formatted(orderId, itemId)));
    }

    @Override
    public OrderStatusDto updateOrderStatus(Long orderId, OrderStatusDto orderStatusDto) {
        Order order = orderRepository.findById(orderId).orElseThrow(()
                -> new EntityNotFoundException("Can't find order by id: " + orderId));
        try {
            order.setStatus(Order.Status.valueOf(orderStatusDto.status().toUpperCase()));
        } catch (IllegalArgumentException ex) {
            throw new InvalidOrderStatusException("Invalid order status: "
                    + orderStatusDto.status());
        }
        return new OrderStatusDto(orderRepository.save(order).getStatus().name());
    }

    private Order createOrder(ShoppingCart shoppingCart, OrderRequestDto orderRequestDto) {
        Order order = orderMapper.toOrderEntityFromShoppingCart(shoppingCart);
        setOrderForOrderItems(order);
        order.setShippingAddress(orderRequestDto.shippingAddress());
        order.setTotal(getTotalOrderItemsPrice(order));
        return order;
    }

    private void setOrderForOrderItems(Order order) {
        Set<OrderItem> orderItems = order.getOrderItems().stream()
                .peek(orderItem -> orderItem.setOrder(order))
                .collect(Collectors.toSet());
        order.setOrderItems(orderItems);
    }

    private BigDecimal getTotalOrderItemsPrice(Order order) {
        return order.getOrderItems().stream()
                .map(OrderItem::getPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
