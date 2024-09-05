package vn.vti.clothing_shop.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import vn.vti.clothing_shop.constants.PaymentMethod;
import vn.vti.clothing_shop.constants.PaymentStatus;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static vn.vti.clothing_shop.constants.RegularExpression.BOOLEAN;
import static vn.vti.clothing_shop.constants.RegularExpression.PHONE_NUMBER;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "`order`")
public class Order implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name= "address",nullable = false)
    private String address;

    @Column(name= "phone_number",columnDefinition = "VARCHAR(255)",nullable = false)
    @Pattern(regexp= PHONE_NUMBER,message = "Invalid phone number")
    private String phone_number;

    @Column(name= "receiver_name",columnDefinition = "VARCHAR(255)",nullable = false)
    private String receiver_name;

    @Column(name= "isPresent", columnDefinition = "BOOLEAN DEFAULT false", nullable = false)
    private Boolean isPresent;

    @Column(name= "total_price", nullable = false)
    private Long total_price;

    @Column(name = "order_code",nullable = false)
    private Long order_code;

    @Column(name= "payment_status", nullable = false)
    @Enumerated(EnumType.STRING)
    private PaymentStatus payment_status;

    @Column(name="payment_method",nullable = false)
    @Enumerated(EnumType.STRING)
    private PaymentMethod payment_method;

    @ManyToOne
    @JoinColumn(name = "user_id",referencedColumnName = "id")
    private User user_id;

    @ManyToOne
    @JoinColumn(name = "voucherId",referencedColumnName = "id")
    private Voucher voucherId;

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime created_at;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updated_at;

    @Column(name = "deleted_at")
    private LocalDateTime deleted_at;
}
