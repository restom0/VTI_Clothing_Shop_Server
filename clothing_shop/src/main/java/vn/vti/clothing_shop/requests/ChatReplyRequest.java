package vn.vti.clothing_shop.requests;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ChatReplyRequest {
    @NotBlank(message = "Vui lòng nhập phản hồi")
    private String reply;
}
