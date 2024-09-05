package vn.vti.clothing_shop.requests;

import jakarta.persistence.Column;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.vti.clothing_shop.entities.OnSaleProduct;
import vn.vti.clothing_shop.entities.Order;
import vn.vti.clothing_shop.entities.User;

import static vn.vti.clothing_shop.constants.RegularExpression.NUMBER;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemCreateRequest {
    private Long product_id;
    private Long order_id;
    private Integer quantity;
}
