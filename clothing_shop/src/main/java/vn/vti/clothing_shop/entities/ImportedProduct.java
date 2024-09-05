package vn.vti.clothing_shop.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import vn.vti.clothing_shop.constants.ClothGender;

import java.io.Serializable;
import java.time.LocalDateTime;

import static vn.vti.clothing_shop.constants.RegularExpression.NUMBER;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "`imported_product`")
public class ImportedProduct implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "product_id",referencedColumnName = "id")
    private Product product_id;

    @ManyToOne
    @JoinColumn(name = "size_id",referencedColumnName = "id")
    private Size size_id;

    @ManyToOne
    @JoinColumn(name = "color_id",referencedColumnName = "id")
    private Color color_id;

    @Column(name = "imageUrl")
    private String image_url;

    @Column(name = "slider_url_1")
    private String slider_url_1;

    @Column(name = "slider_url_2")
    private String slider_url_2;

    @Column(name = "slider_url_3")
    private String slider_url_3;

    @Column(name = "slider_url_4")
    private String slider_url_4;

    @Column(name = "public_id_url")
    private String public_id_url;

    @Column(name = "public_id_slider_url_1")
    private String public_id_slider_url_1;

    @Column(name = "public_id_slider_url_2")
    private String public_id_slider_url_2;

    @Column(name = "public_id_slider_url_3")
    private String public_id_slider_url_3;

    @Column(name = "public_id_slider_url_4")
    private String public_id_slider_url_4;

    @ManyToOne
    @JoinColumn(name = "material_id",referencedColumnName = "id")
    private Material material_id;

    @Column(name = "sku")
    private String sku;

    @Column(name = "gender")
    @Enumerated(EnumType.STRING)
    private ClothGender gender;

    @NotNull(message = "Import price is required")
    @Column(name = "importPrice",nullable = false)
    @Min(value = 0, message = "Import price must be positive")
    private Integer importPrice;

    @Min(value = 1, message = "Import number must be greater than 0")
    @NotNull(message = "Import number is required")
    private Integer importNumber;

    @NotNull(message = "Stock is required")
    @Min(value = 0, message = "Stock must be positive")
    @Column(name = "stock",nullable = false)
    private Integer stock;

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime created_at;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updated_at;

    @Column(name = "deleted_at",columnDefinition = "TIMESTAMP")
    private LocalDateTime deleted_at;
}