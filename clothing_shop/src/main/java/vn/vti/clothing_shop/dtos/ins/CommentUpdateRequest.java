package vn.vti.clothing_shop.dtos.ins;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

public record CommentUpdateRequest(
        @NotNull(message = "{messages.validation.required}")
        @Positive
        Long productId,

        @NotBlank(message = "{messages.validation.required}")
        @Size(max = 255, message = "{messages.validation.max255}")
        String content,

        @PositiveOrZero
        @DecimalMax(value = "5.0", message = "{messages.validation.rating.invalid}")
        @NotNull(message = "{messages.validation.required}")
        Float star,

        @NotNull
        Long version
) {
}
