package vn.vti.clothing_shop.dtos.outs;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class InputSaleDTO {
    List<OnSaleProductDTO> onSaleProducts;
    private Long id;
    private String filter;
    private Long filterId;
    private Float salePercentage;
    private Float discount;
    private LocalDate availableDate;
    private LocalDate endDate;
}
