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
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "`input_sale`")
public class InputSale implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "filter")
    @Enumerated(EnumType.STRING)
    private InputSaleFilter filter;

    @Min(value = 1, message = "Filter id must be greater than 0")
    @Max(value = Long.MAX_VALUE, message = "Filter id must be less than or equal to " + Long.MAX_VALUE)
    @Column(name = "filter_id")
    private Long filter_id;

    @NotNull(message = "Sale percentage is required")
    @DecimalMin(value = "0.00", message = "Sale percentage must be greater than 0")
    @Column(name = "sale_percentage", columnDefinition = "NUMERIC(10,2)", nullable = false)
    private Float salePercentage;

    @NotNull(message = "Discount is required")
    @DecimalMin(value = "0.00", message = "Discount must be greater than 0")
    @DecimalMax(value = "100.00", message = "Discount must be less than or equal to 100")
    @Column(name = "discount", columnDefinition = "NUMERIC(10,2)", nullable = false)
    private Float discount;

    //@Future(message = "Available date must be in the future")
    @NotNull(message = "Available date is required")
    @Column(name = "available_date", nullable = false)
    private LocalDateTime available_date;

    @Future(message = "End date must be in the future")
    @Column(name = "end_date")
    private LocalDateTime end_date;

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime created_at;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updated_at;

    @Column(name = "deleted_at")
    private LocalDateTime deleted_at;
}