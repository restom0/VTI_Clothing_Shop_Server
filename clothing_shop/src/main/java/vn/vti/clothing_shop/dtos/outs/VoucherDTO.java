package vn.vti.clothing_shop.dtos.outs;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class VoucherDTO {
    private Long id;
    private Integer stock;
    private Integer value;
    private String code;
    private LocalDateTime available_date;
    private LocalDateTime expired_date;
}
