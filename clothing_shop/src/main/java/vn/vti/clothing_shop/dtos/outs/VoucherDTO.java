package vn.vti.clothing_shop.dtos.outs;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class VoucherDTO {
    private Long id;
    private Integer stock;
    private Float value;
    private String code;
    private Long availableDate;
    private Long endDate;
}
