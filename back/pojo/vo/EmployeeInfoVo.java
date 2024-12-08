package pojo.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import pojo.Employee;

import java.util.Date;
@AllArgsConstructor
@NoArgsConstructor
@Data
public class EmployeeInfoVo extends Employee {
    private Integer id;
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
    private String depName;
    private String jobName;
}