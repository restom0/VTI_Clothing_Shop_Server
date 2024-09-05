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
public class VoucherCreateDTO {
    private String code;
    private Integer input_stock;
    private Float value;
    private LocalDateTime available_date;
    private LocalDateTime expired_date;
}
