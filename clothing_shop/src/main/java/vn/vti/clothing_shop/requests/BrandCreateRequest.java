package vn.vti.clothing_shop.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@NotNull(message = "Vui lòng nhập tên thương hiệu")
public class BrandCreateRequest {
    @NotBlank(message = "Vui lòng nhập tên thương hiệu")
    private String name;
    private String description;
}
