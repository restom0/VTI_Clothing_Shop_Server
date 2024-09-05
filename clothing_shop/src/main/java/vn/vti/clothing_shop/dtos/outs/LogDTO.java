package vn.vti.clothing_shop.dtos.outs;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import vn.vti.clothing_shop.constants.Filter;
import vn.vti.clothing_shop.entities.User;

import java.time.LocalDateTime;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class LogDTO {
    private Long id;
    private UserDTO user_id;
    private String method;
    private Filter column;
    private String detail;
    private LocalDateTime created_at;
}
