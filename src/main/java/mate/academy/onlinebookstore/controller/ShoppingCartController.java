package mate.academy.onlinebookstore.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import mate.academy.onlinebookstore.dto.cartitem.CartItemRequestDto;
import mate.academy.onlinebookstore.dto.cartitem.CartItemUpdateQuantityDto;
import mate.academy.onlinebookstore.dto.shoppingcart.ShoppingCartDto;
import mate.academy.onlinebookstore.service.shoppingcart.ShoppingCartService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Shopping cart management", description = "Endpoints for managing shopping cart")
@RestController
@RequestMapping("/cart")
@RequiredArgsConstructor
public class ShoppingCartController {
    private final ShoppingCartService shoppingCartService;

    @Operation(summary = "get shopping cart", description = "get shopping cart for current user")
    @PreAuthorize("hasRole('USER')")
    @GetMapping
    public ShoppingCartDto getShoppingCart() {
        return shoppingCartService.getShoppingCart();
    }

    @Operation(summary = "add book to shopping cart", description = "add book to shopping cart")
    @PreAuthorize("hasRole('USER')")
    @PostMapping
    public ShoppingCartDto addBookToShoppingCart(
            @RequestBody @Valid CartItemRequestDto requestDto
    ) {
        return shoppingCartService.addBookToShoppingCart(requestDto);
    }

    @Operation(
            summary = "update cart item quantity by id",
            description = "update cart item quantity by id"
    )
    @PreAuthorize("hasRole('USER')")
    @PutMapping("/{id}")
    public ShoppingCartDto updateCartItemQuantityById(
            @PathVariable Long id,
            @RequestBody @Valid CartItemUpdateQuantityDto updateQuantityDto
    ) {
        return shoppingCartService.updateCartItemQuantityById(id, updateQuantityDto);
    }

    @Operation(summary = "delete cart item by id", description = "delete cart item by id")
    @PreAuthorize("hasRole('USER')")
    @DeleteMapping("/{id}")
    public ShoppingCartDto deleteCartItemById(@PathVariable Long id) {
        return shoppingCartService.deleteCartItemById(id);
    }
}
