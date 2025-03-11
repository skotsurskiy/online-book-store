package mate.academy.onlinebookstore.dto.order;

public record UpdateOrderStatusDto(
        Long orderId,
        String status
) {
}
