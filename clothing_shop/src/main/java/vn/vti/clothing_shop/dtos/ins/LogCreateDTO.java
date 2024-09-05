package vn.vti.clothing_shop.dtos.ins;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.vti.clothing_shop.constants.Filter;
import vn.vti.clothing_shop.dtos.outs.UserDTO;

import java.time.LocalDateTime;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class LogCreateDTO {
    private Long id;
    private Long user_id;
    private String method;
    private Filter column;
    private String detail;
}
