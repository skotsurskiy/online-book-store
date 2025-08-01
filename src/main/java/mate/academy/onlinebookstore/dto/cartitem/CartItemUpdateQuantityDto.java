package mate.academy.onlinebookstore.dto.cartitem;

import jakarta.validation.constraints.Positive;

public record CartItemUpdateQuantityDto(@Positive int quantity) {
}

