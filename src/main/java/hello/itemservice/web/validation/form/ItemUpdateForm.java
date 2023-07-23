package hello.itemservice.web.validation.form;

import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotNull;

@Data
public class ItemUpdateForm {
    @NotNull //수정폼에서는 아이디 값이 필요하다
    private Long id;
    @NotBlank
    private String itemName;

    @NotNull
    @Range(min = 1000, max = 1000000)
    private Integer price;
    
    //수정폼에서는 최대수량조절이 필요없다  왜냐하면 자유롭게 변경할수 있어야 하므로
    private Integer quantity;
}
