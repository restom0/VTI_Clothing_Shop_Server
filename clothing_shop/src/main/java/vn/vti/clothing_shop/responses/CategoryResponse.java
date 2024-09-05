package vn.vti.clothing_shop.responses;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.vti.clothing_shop.dtos.outs.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CategoryResponse {
    private CategoryDTO categoryDTO;
    private List<ColorDTO> colorDTO;
    private List<SizeDTO> sizeDTO;
    private List<MaterialDTO> materialDTO;
}
