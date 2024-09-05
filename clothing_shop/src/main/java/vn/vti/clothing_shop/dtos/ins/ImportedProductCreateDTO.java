package vn.vti.clothing_shop.dtos.ins;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.vti.clothing_shop.constants.ClothGender;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ImportedProductCreateDTO {
    private Long product_id;
    private ColorCreateDTO color;
    private SizeCreateDTO size;
    private MaterialCreateDTO material;
    private Integer importNumber;
    private ClothGender gender;
    private Integer importPrice;
    private String image_url;
    private String slider_url_1;
    private String slider_url_2;
    private String slider_url_3;
    private String slider_url_4;
    private String public_id_url;
    private String public_id_slider_url_1;
    private String public_id_slider_url_2;
    private String public_id_slider_url_3;
    private String public_id_slider_url_4;
}
