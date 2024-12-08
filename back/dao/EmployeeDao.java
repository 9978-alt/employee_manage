package dao;

import pojo.Employee;
import pojo.vo.EmployeeInfoVo;
import pojo.vo.Uservo;

import java.util.List;

public interface EmployeeDao {

    /**
     * 根据用户ID在数据库中查询该用户
     * @return  如果查询到该用户，那么返回该用户，否则，返回null
     */
    Employee FindEmployeeById(Integer id);

    /**
     * 根据用户id查询用户的完整信息
     * 所属部门和工作名称都用String修饰
     * @param id    需要查找的用户id
     * @return  返回一个EmployeeInfoVo对象
     */
    EmployeeInfoVo FindEmpolyeeAllInfoById(int id);

    /**
     * 修改员工的基本信息
     * @param employeeInfoVo 需要修改数据的员工的所有数据
     * @return 受影响的行数
     */
    int AlterInfoAll(EmployeeInfoVo employeeInfoVo);

    /**
     *  修改员工密码
     * @param uservo 要修改的员工的用户信息
     * @return  返回受影响的行数
     */
    int alterPasswordById(Uservo uservo);

    /**
     * 获取员工的薪资
     * @param id 员工的id
     * @return  返回员工的薪资
     */
    int findSalaryById(int id);

    /**
     * 更换指定id的员工的工作
     * @param id    要更换的员工的id
     * @param jobId 该员工新的工作
     * @return  返回受影响的行数
     */
    int changeJobById(Integer id, Integer jobId);

    /**
     * 查询指定部门的所有人员信息
     * @param depId 部门id
     * @return  返回一个由员工组成的列表集合
     */
    List<EmployeeInfoVo> getEmployeeList(int depId);

    /**
     * 根据id删除指定员工
     * @param id    要删除的员工的id
     * @return  返回受影响的行数
     */
    int delEmployeeById(int id);

    /**
     * 获取姓名为name的所有员工
     * @param name  要查询的姓名
     * @return  返回员工信息的列表集合
     */
    List<EmployeeInfoVo> getEmployeeListByName(String name,int id);

    /**
     * 添加一个employee对象信息
     * @param employee  employee对象
     * @return  返回受影响的行数
     */
    int addEmployee(Employee employee);
}
