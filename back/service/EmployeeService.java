package service;

import pojo.Employee;
import pojo.User;
import pojo.vo.EmployeeInfoVo;
import pojo.vo.Uservo;

import java.util.List;

public interface EmployeeService {

    /**
     *  判断登录用户是否在数据库中
     *  如果在数据库中判断该用户的密码是否正确
     * @param employee  登录的用户信息
     * @return  如果用户信息正确，返回true，如果不正确，返回false
     */
    boolean CheckEmployeeInfo(User employee);

    /**
     * 根据用户id查找对应用户
     * @param id    需要查找的用户id
     * @return  如果用户存在返回employee对象，否则返回null
     */
    EmployeeInfoVo FindEmployeeById(int id);

    /**
     * 修改员工的基本信息
     * @param employeeInfoVo 需要修改数据的员工的所有数据
     * @return
     */
    int AlterInfoOfEmplpoyee(EmployeeInfoVo employeeInfoVo);

    /**
     * 完成员工修改密码的要求
     * @param uservo 包含了id，原始密码和新密码
     * @return 返回受影响的行数
     */
    int alterPassword(Uservo uservo);

    /**
     * 实现普通员工查询薪资的业务接口
     * @param id 查询的id
     * @return  返回薪资数量
     */
    int FindSalaryById(int id);

    /**
     * 查询指定部门的所有人人员信息
     * @param depId 部门id
     * @return  以集合的形式返回所有员工信息
     */
    List<EmployeeInfoVo> getEmployeeListByDep(int depId);

    /**
     * 根据id删除一个数据
     * @param id  要删除的员工id
     * @return  返回受影响的行数
     */
    int delEmployeeById(int id);

    /**
     * 根据名字查找员工
     * @param name 姓名
     * @return 返回员工列表的集合
     */
    List<EmployeeInfoVo> getEmployeeByName(String name,int id);

    /**
     * 添加employee对象
     * @param employee  employee对象
     * @return  返回受影响的行数
     */
    int addEmployee(Employee employee);
}
