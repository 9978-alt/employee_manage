package pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class Employee extends User implements Serializable {
    private  Integer id;
    private String name;
    private String passwd;
    private String sex;
    private Integer rate;
    private Date birth;
    private String address;
    private String cardId;
    private String tel;
    private Integer depId;
    private Integer jobId;
}
