package vn.vti.clothing_shop.dtos.outs;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OnSaleProductDTO {
    private Long id;
    private ImportedProductDTO product_id;
    private float sale_price;
    private float discount;
}
