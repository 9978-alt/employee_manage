package pojo.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class DepartmentQueryVo {
    private Integer id;
    private String depName;
    private Integer empId;
    private String empName;
}
