package vn.vti.clothing_shop.dtos.ins;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.vti.clothing_shop.dtos.outs.ProductDTO;
import vn.vti.clothing_shop.dtos.outs.UserDTO;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CommentUpdateDTO {
    private Long id;
    private UserDTO user_id;
    private ProductDTO product_id;
    private String content;
    private Boolean status;
    private Float star;
}
