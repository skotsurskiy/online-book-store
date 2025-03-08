package mate.academy.onlinebookstore.dto.order;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

public record UpdateOrderStatusDto(
        @Positive
        Long orderId,
        @NotBlank
        String status
) {
}
