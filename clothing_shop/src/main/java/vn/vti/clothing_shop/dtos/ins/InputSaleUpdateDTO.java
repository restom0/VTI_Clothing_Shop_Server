package vn.vti.clothing_shop.dtos.ins;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class InputSaleUpdateDTO {
    private Long id;
    private Float salePercentage;
    private Float discount;
    private LocalDateTime available_date;
    private LocalDateTime end_date;
}
