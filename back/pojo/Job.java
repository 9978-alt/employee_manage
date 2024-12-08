package pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class Job implements Serializable {
    private  Integer id;
    private String jobName;
    private Integer salary;
    private Integer depId;
}
