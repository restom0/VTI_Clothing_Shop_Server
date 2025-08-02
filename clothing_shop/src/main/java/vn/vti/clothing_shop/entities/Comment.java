package vn.vti.clothing_shop.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
public class Comment extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private User userId;

    @ManyToOne
    private Product productId;

    @Column(nullable = false)
    private String content;

    private Boolean status;

    @Column(
            nullable = false,
            precision = 2,
            scale = 1,
            columnDefinition = "DECIMAL(2,1) CHECK (star >= 0 AND star <= 5)"
    )
    private Float star;

}
