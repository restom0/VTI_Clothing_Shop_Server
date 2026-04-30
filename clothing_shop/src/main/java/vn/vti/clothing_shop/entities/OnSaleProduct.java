package vn.vti.clothing_shop.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Transient;
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

    @Transient
    private Long salePrice;

    @ManyToOne
    @JoinColumn
    private ImportedProduct product;

    @ManyToOne
    @JoinColumn
    private InputSale inputSale;

    public Long getSalePrice() {
        if (salePrice != null) {
            return salePrice;
        }
        if (product == null || product.getImportPrice() == null || inputSale == null || inputSale.getSalePercentage() == null) {
            return 0L;
        }
        return Math.round(product.getImportPrice() * inputSale.getSalePercentage() / 100.0);
    }
}
