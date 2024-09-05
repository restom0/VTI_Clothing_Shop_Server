package vn.vti.clothing_shop.dtos.outs;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CommentDTO {
    private Long id;
    private UserDTO user_id;
    private ProductDTO product_id;
    private String content;
    private Boolean status;
    private Float star;
    private LocalDateTime created_at;
}
