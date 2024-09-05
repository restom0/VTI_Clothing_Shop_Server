package vn.vti.clothing_shop.requests;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CategoryUpdateRequest {
    @NotBlank(message = "Vui lòng nhập tên thương hiệu")
    private String name;

    private String description;
}
