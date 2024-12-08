package dao;

import pojo.Department;

public interface DepartmentDao {
    /**
     * 根据主管id找出他所属的部门
     * @param id 主管id
     * @return 如果找到了就返回一个Department对象，如果没找到就返回null
     */
    Department FindDepartmentManagerById(Integer id);
}
