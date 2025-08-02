package vn.vti.clothing_shop.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import vn.vti.clothing_shop.constants.InputSaleFilter;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class InputSale extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private InputSaleFilter filter;

    private Long filterId;

    @Column(nullable = false, precision = 3, columnDefinition = "CHECK sale_percentage >= 0")
    private Integer salePercentage;

    @Column(nullable = false, columnDefinition = "TINYINT CHECK (discount >= 0 AND discount <= 100)")
    private Byte discount;

    @Column(nullable = false)
    private LocalDate startDate;

    private LocalDate endDate;

}