package mate.academy.onlinebookstore.dto.cartitem;

import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CartItemUpdateQuantityDto {
    @Positive
    private int quantity;
}
