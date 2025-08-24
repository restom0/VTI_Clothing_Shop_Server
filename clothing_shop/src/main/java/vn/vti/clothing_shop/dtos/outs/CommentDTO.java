package vn.vti.clothing_shop.dtos.outs;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CommentDTO {
    private Long id;
    private UserDTO userId;
    private ProductDTO productId;
    private String content;
    private Boolean status;
    private Float star;
    private Long createdAt;
}
