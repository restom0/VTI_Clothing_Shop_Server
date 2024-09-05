package vn.vti.clothing_shop.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Pattern;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.io.Serializable;
import java.time.LocalDateTime;

import static vn.vti.clothing_shop.constants.RegularExpression.COLOR;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name="`color`",uniqueConstraints = {
        @UniqueConstraint(columnNames = {"color", "category_id"}),
        @UniqueConstraint(columnNames = {"name", "category_id"})
})
public class Color implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "color_code",unique = true,nullable = false)
    @Pattern(regexp = COLOR, message = "Invalid color code")
    private String color_code;

    @Column(name = "color_name",unique = true,nullable = false)
    private String color_name;

    @ManyToOne
    @JoinColumn(name = "category_id",referencedColumnName = "id")
    private Category category_id;

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime created_at;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updated_at;

    @Column(name = "deleted_at",columnDefinition = "TIMESTAMP")
    private LocalDateTime deleted_at;
}
