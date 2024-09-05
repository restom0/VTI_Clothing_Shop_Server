package vn.vti.clothing_shop.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import vn.vti.clothing_shop.constants.UserGender;
import vn.vti.clothing_shop.constants.UserRole;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

import static vn.vti.clothing_shop.constants.RegularExpression.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "`user`")
public class User implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="name",nullable = false)
    private String name;

    @Column(name="username",unique = true,nullable = false)
    private String username;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "birthday")
    private LocalDate birthday;

    @Column(name = "avatar_url")
    private String avatar_url;

    @Column(name = "public_id_avatar_url")
    private String public_id_avatar_url;

    @Column(name = "email",unique = true)
    @Pattern(regexp = EMAIL,message = "Invalid email")
    private String email;

    @Column(name = "phone_number",unique = true,nullable = false)
    @Pattern(regexp = PHONE_NUMBER,message = "Invalid phone number")
    private String phone_number;

    @Column(name = "role")
    @Enumerated(EnumType.STRING)
    private UserRole role;

    @Column(name="address")
    private String address;

    @Column(name="salt")
    private String salt;

    @Column(name = "gender")
    @Enumerated(EnumType.STRING)
    private UserGender gender;

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime created_at;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updated_at;

    @Column(name = "deleted_at")
    private LocalDateTime deleted_at;
}
