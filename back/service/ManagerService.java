package service;

import pojo.Employee;
import pojo.Manager;
import pojo.User;
import pojo.vo.DepartmentQueryVo;
import pojo.vo.EmployeeInfoVo;
import pojo.vo.Uservo;

import java.util.List;

public interface ManagerService {
    /**
     * 检查管理员用户的登录信息是否正确
     * @param manager 管理员用户
     * @return 如果找到了该用户并且密码正确，返回true，如果不正确或者不存在，返回false
     *
     */
    boolean CheckManagerInfo(User manager);

    /**
     *实现登录后自动获取部门信息的请求
     * @return 返回客户端所有部门的信息的列表：id，name,empId,empName
     */
    List<DepartmentQueryVo> getDedault();

    /**
     * 根据用户id查找用户的信息
     * @param id 当前管理员用户的id
     * @return 如果存在返回一个Manager对象，如果不存在，返回null
     */
    Manager getManagerInfo(int id);

    /**
     * 实现修改用户密码的需求
     * @param uservo  用户信息
     * @return  返回数据库中受影响的行数
     */
    int alterManagerPasswd(Uservo uservo);

    /**
     * 备份数据库
     */
    void backup();

    /**
     * 获取指定部门的所有员工信息
     * @return  返回该部门的所有员工的信息
     */
    List<EmployeeInfoVo> getEmployeeOfDepId(int id);

    /**
     * 实现更换指定部门的主管的信息
     */
    void changeManager(EmployeeInfoVo employeeInfoVo);
}
