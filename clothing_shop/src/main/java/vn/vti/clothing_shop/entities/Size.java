package vn.vti.clothing_shop.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.io.Serializable;
import java.time.LocalDateTime;

import static vn.vti.clothing_shop.constants.RegularExpression.NUMBER;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "`size`",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"size", "category_id"}),
                @UniqueConstraint(columnNames = {"weight", "category_id"}),
                @UniqueConstraint(columnNames = {"height", "category_id"})})
public class Size implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "size",nullable = false)
    private String size;

    @Column(name = "height",nullable = false)
    private String height;

    @Column(name = "weight",nullable = false)
    private String weight;

    @ManyToOne
    @JoinColumn(name = "category_id",referencedColumnName = "id")
    private Category category_id;

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime created_at;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updated_at;

    @Column(name = "deleted_at")
    private LocalDateTime deleted_at;
}
