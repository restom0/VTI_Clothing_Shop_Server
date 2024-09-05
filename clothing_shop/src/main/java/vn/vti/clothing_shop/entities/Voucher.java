package vn.vti.clothing_shop.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "`voucher`")
public class Voucher implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="code",unique = true,nullable = false)
    private String code;

    @Column(name="available_date",nullable = false)
    private LocalDateTime available_date;

    @Column(name="expired_date",nullable = false)
    private LocalDateTime expired_date;

    @Column(name="input_stock",nullable = false)
    private Integer input_stock;

    @Column(name="stock",nullable = false)
    private Integer stock;

    @Column(name="value",nullable = false)
    @DecimalMin(value = "0.00", message = "Value must be greater than 0")
    @DecimalMax(value = "100.00", message = "Value must be less than 100")
    private Float value;

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime created_at;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updated_at;

    @Column(name = "deleted_at")
    private LocalDateTime deleted_at;
}
