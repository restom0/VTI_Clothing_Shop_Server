package vn.vti.clothing_shop.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.vti.clothing_shop.constants.ClothGender;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class ImportedProduct extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn
    private Product product;

    @ManyToOne
    @JoinColumn
    private Size size;

    @ManyToOne
    @JoinColumn
    private Color color;

    private String imageUrl;

    private String sliderUrl1;

    private String sliderUrl2;

    private String sliderUrl3;

    private String sliderUrl4;

    private String publicIdUrl;

    private String publicIdSliderUrl1;

    private String publicIdSliderUrl2;

    private String publicIdSliderUrl3;

    private String publicIdSliderUrl4;

    @ManyToOne
    @JoinColumn
    private Material material;

    private String sku;

    @Enumerated(EnumType.STRING)
    private ClothGender gender;

    @Column(nullable = false)
    private Integer importPrice;

    @Column(nullable = false)
    private Integer importNumber;

    @Column(nullable = false)
    private Integer stock;
}