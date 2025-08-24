package vn.vti.clothing_shop.dtos.outs;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class VoucherAdminDTO {
    private Long id;
    private Integer inputStock;
    private Integer stock;
    private Integer value;
    private String code;
    private LocalDate availableDate;
    private LocalDate endDate;
}
