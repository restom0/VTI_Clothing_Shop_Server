package vn.vti.clothing_shop.dtos.ins;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OnSaleProductUpdateDTO {
    private Long id;
    private Long product_id;
    private float sale_price;
    private float discount;
}
