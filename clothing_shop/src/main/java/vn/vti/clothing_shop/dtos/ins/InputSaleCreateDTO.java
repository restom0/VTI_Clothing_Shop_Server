package vn.vti.clothing_shop.dtos.ins;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.vti.clothing_shop.constants.InputSaleFilter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class InputSaleCreateDTO {
    private InputSaleFilter filter;
    private Long filter_id;
    private Float salePercentage;
    private Float discount;
    private LocalDateTime available_date;
    private LocalDateTime end_date;
}
