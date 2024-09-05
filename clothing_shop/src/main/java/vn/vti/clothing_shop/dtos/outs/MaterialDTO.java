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
@JsonTypeName("MaterialDTO")
public class MaterialDTO extends BaseDTO {
    private Long id;
    private String name;
    private CategoryDTO category_id;
}