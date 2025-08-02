package vn.vti.clothing_shop.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class OnSaleProduct extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn
    private ImportedProduct productId;

    @Column(nullable = false, columnDefinition = "BIGINT CHECK (sale_price >= 0)")
    private Long salePrice;

    @Column(columnDefinition = "INT DEFAULT 0 CHECK (discount >= 0 AND discount <= 100)")
    private Byte discount;

    @ManyToOne
    @JoinColumn
    private InputSale inputSaleId;

}