package vn.vti.clothing_shop.requests;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import static vn.vti.clothing_shop.constants.RegularExpression.STAR_RATING;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CommentUpdateRequest {
    @NotBlank(message = "Vui lòng chọn sản phẩm")
    private Long product_id;

    @NotBlank(message = "Vui lòng nhập nội dung")
    private String content;

    @DecimalMin(value = "1.0", message = "Số sao không hợp lệ")
    @DecimalMax(value = "5.0", message = "Số sao không hợp lệ")
    @NotBlank(message = "Vui lòng nhập đánh giá")
    private Float star;
}
