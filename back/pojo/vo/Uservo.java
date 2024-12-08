package pojo.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Uservo {
    private Integer id;
    private String originPasswd;
    private String newPasswd;
}
