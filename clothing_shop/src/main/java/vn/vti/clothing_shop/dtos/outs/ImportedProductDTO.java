package vn.vti.clothing_shop.dtos.outs;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.vti.clothing_shop.constants.ClothGender;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ImportedProductDTO {
    private Long id;
    private ProductDTO product;
    private Integer importNumber;
    private SizeDTO size;
    private ColorDTO color;
    private MaterialDTO material;
    private String imageUrl;
    private String sliderUrl1;
    private String sliderUrl2;
    private String sliderUrl3;
    private String sliderUrl4;
    private String publicIdUrl;
    private String publicIdSliderUrl1;
    private String publicIdSliderUrl2;
    private String publicIdSliderUrl3;
    private String publicIdSliderUrl4;
    private String sku;
    private ClothGender gender;
    private Integer importPrice;
    private Integer stock;
}