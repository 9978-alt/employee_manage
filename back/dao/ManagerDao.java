package dao;

import pojo.Department;
import pojo.Employee;
import pojo.Manager;
import pojo.vo.DepartmentQueryVo;
import pojo.vo.EmployeeInfoVo;
import pojo.vo.Uservo;

import java.util.List;

public interface ManagerDao {
    /**
     * 根据id查找管理员信息
     * @param id 登陆的id信息
     * @return 如果存在该id就返回一个manager对象，否则返回null
     */
    Manager FindManagerById(Integer id);

    /**
     * 联合department和employee 查找部门以及部门的负责人信息
     * @return 返回所有信息的列表集合
     */
    List<DepartmentQueryVo> getDefault();

    /**
     * 根据id在manger表中查询当前用户的信息
     * @param id    当前用户的id
     * @return 如果存在返回一个Manager对象，如果不存在，返回null
     */
    Manager findManagerById(int id);

    /**
     * 修改用户密码
     * @param uservo 用户id和新密码
     * @return 返回受影响的行数
     */
    int alterPasswd(Uservo uservo);

    /**
     * 使用mysqldump对数据库进行备份
     */
    void backup();

    /**
     * 根据指定部门id查询所有该部门的所有员工信息
     * @param id   部门id
     * @return  以List的形式返回该部门的所有员工，
     */
    List<EmployeeInfoVo> findAllemployeeByDepId(int id);

    /**
     * 根据部门名称找到对应的部门信息
     * @param depName   部门名称
     * @return  返回Department对象
     */
    Department findDepartmentByName(String depName);

    /**
     * 更换指定部门id的主管id
     * @param id 要更换的部门id
     * @param managerId 新主管的id
     * @return  返回受影响的行数
     */
    int changeManager(Integer id, Integer managerId);
}
