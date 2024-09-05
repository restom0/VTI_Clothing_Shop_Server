package vn.vti.clothing_shop.dtos.outs;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonTypeName("SizeDTO")
public class SizeDTO extends BaseDTO {
    private Long id;
    private String size;
    private String height;
    private String weight;
    private CategoryDTO category_id;
}