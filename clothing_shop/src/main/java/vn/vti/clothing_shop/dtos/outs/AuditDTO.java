package vn.vti.clothing_shop.dtos.outs;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.vti.clothing_shop.constants.Filter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AuditDTO {
    private Long id;
    private UserDTO user;
    private String method;
    private Filter column;
    private String detail;
    private Long createdAt;
}
