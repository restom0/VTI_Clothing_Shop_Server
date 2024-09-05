package vn.vti.clothing_shop.entities;
import jakarta.persistence.*;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import vn.vti.clothing_shop.constants.Filter;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "`log`")
public class Log implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id",referencedColumnName = "id")
    private User user_id;

    @Column(name = "`method`")
    private String method;

    @Column(name = "`column`")
    @Enumerated(EnumType.STRING)
    private Filter column;

    @Column(name = "`detail`")
    private String detail;

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime created_at;
}
