package vn.vti.clothing_shop.dtos.outs;

import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonTypeName("ColorDTO")
public class ColorDTO extends BaseDTO {
    private Long id;
    private String color_code;
    private String color_name;
    private CategoryDTO category_id;
}